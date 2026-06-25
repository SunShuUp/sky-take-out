package com.sky.until;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 * 结构：1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
 * 生成64位Long型ID，转换为字符串后可作为订单号使用
 */
@Component
public class SnowflakeIdGenerator {

    // ============ 常量配置 ============

    /** 起始时间戳：2024-01-01 00:00:00 (可根据项目上线时间调整) */
    private static final long START_TIMESTAMP = 1704067200000L;

    /** 机器ID所占位数 (10位 = 5位数据中心 + 5位机器) */
    private static final long MACHINE_ID_BITS = 10L;

    /** 序列号所占位数 (12位，每毫秒最多生成4096个ID) */
    private static final long SEQUENCE_BITS = 12L;

    /** 最大机器ID = 1023 (2^10 - 1) */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    /** 最大序列号 = 4095 (2^12 - 1) */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /** 机器ID左移位数 = 12 */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /** 时间戳左移位数 = 12 + 10 = 22 */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;


    // ============ 成员变量 ============

    /** 机器ID (0 ~ 1023) */
    private final long machineId;

    /** 当前毫秒内的序列号 (0 ~ 4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间戳 */
    private long lastTimestamp = -1L;


    // ============ 构造函数 ============

    /**
     * @param machineId 机器ID，范围 0 ~ 1023
     *                  在分布式环境中，每个节点必须分配不同的ID
     */
    public SnowflakeIdGenerator(@Value("${snowflake.machine-id:1}")long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(
                    "Machine ID must be between 0 and " + MAX_MACHINE_ID
            );
        }
        this.machineId = machineId;
    }


    // ============ 核心方法 ============

    /**
     * 生成唯一ID (线程安全)
     * @return 64位Long型ID
     */
    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();

        // ----- 时钟回拨检查 -----
        if (currentTimestamp < lastTimestamp) {
            // 发生时钟回拨，等待时间追上
            long offset = lastTimestamp - currentTimestamp;
            if (offset <= 5000) {
                // 回拨小于5秒，等待追上
                try {
                    Thread.sleep(offset);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                currentTimestamp = System.currentTimeMillis();
            } else {
                // 回拨超过5秒，直接抛异常（需要人工介入）
                throw new RuntimeException(
                        "Clock moved backwards. Refusing to generate ID."
                );
            }
        }

        // ----- 同一毫秒内：序列号自增 -----
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出（超过4095），等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒：序列号重置为0
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // ----- 组装64位ID -----
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待直到下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}