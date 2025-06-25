-- -----------------------------------------------------
-- 表: `job_application` (投递记录表) - 【全新】
-- 职责: 记录用户对岗位的投递行为，是招聘流程的核心
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `job_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '投递用户的ID',
  `job_post_id` BIGINT NOT NULL COMMENT '被投递的岗位ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT '投递状态: SUBMITTED-已投递, VIEWED-已查看, INTERVIEWING-面试中, OFFERED-已录用, REJECTED-不合适',
  `application_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_user_job` (`user_id` ASC, `job_post_id` ASC) VISIBLE COMMENT '确保一个用户对一个岗位只能投递一次',
  INDEX `idx_job_post_id` (`job_post_id` ASC) VISIBLE,
  CONSTRAINT `fk_application_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_application_job`
    FOREIGN KEY (`job_post_id`)
    REFERENCES `job_post` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE = InnoDB COMMENT = '用户投递岗位记录表';