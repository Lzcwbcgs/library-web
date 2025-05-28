package com.xiazihan.webback.task;

import com.xiazihan.webback.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BorrowTask {

    private final BorrowService borrowService;

    /**
     * 每天凌晨1点执行一次，处理逾期记录
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void handleOverdueRecords() {
        log.info("开始处理逾期记录");
        try {
            borrowService.handleOverdueRecords();
            log.info("处理逾期记录完成");
        } catch (Exception e) {
            log.error("处理逾期记录失败", e);
        }
    }
}