-- =========================================================
-- 예산 집행 관리 미니 ERP - DB (MySQL 8)
-- 기준 정보: 부서, 직원, 예산 과목, 예산 편성
-- 이력 정보: 집행 신청, 결재 이력
-- =========================================================
DROP DATABASE IF EXISTS budget_erp;
CREATE DATABASE budget_erp DEFAULT CHARACTER SET utf8mb4;
USE budget_erp;

-- ---------- 기준 정보 ----------
CREATE TABLE dept (
    dept_id     INT AUTO_INCREMENT PRIMARY KEY,
    dept_name   VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE employee (
    emp_id      INT AUTO_INCREMENT PRIMARY KEY,
    dept_id     INT         NOT NULL,
    emp_name    VARCHAR(50) NOT NULL,
    role        VARCHAR(10) NOT NULL DEFAULT '담당자',   -- 담당자 / 결재자
    FOREIGN KEY (dept_id) REFERENCES dept(dept_id),
    CHECK (role IN ('담당자','결재자'))
);

-- 예산 과목 (예: 사무용품비, 출장비, 교육비)
CREATE TABLE budget_item (
    item_id     INT AUTO_INCREMENT PRIMARY KEY,
    item_code   VARCHAR(10) NOT NULL UNIQUE,
    item_name   VARCHAR(50) NOT NULL
);

-- 예산 편성: 연도 x 부서 x 과목 하나당 금액 하나
CREATE TABLE budget (
    budget_id   INT AUTO_INCREMENT PRIMARY KEY,
    fiscal_year INT    NOT NULL,
    dept_id     INT    NOT NULL,
    item_id     INT    NOT NULL,
    amount      BIGINT NOT NULL,
    UNIQUE (fiscal_year, dept_id, item_id),
    FOREIGN KEY (dept_id) REFERENCES dept(dept_id),
    FOREIGN KEY (item_id) REFERENCES budget_item(item_id),
    CHECK (amount >= 0)
);

-- ---------- 이력 정보 ----------
-- 집행 신청
CREATE TABLE expense (
    expense_id  INT AUTO_INCREMENT PRIMARY KEY,
    budget_id   INT          NOT NULL,
    emp_id      INT          NOT NULL,              -- 신청자
    title       VARCHAR(100) NOT NULL,
    amount      BIGINT       NOT NULL,
    req_date    DATE         NOT NULL,
    status      VARCHAR(10)  NOT NULL DEFAULT '대기', -- 대기 / 승인 / 반려
    FOREIGN KEY (budget_id) REFERENCES budget(budget_id),
    FOREIGN KEY (emp_id)    REFERENCES employee(emp_id),
    CHECK (amount > 0),
    CHECK (status IN ('대기','승인','반려'))
);

-- 결재 이력: 상태가 바뀔 때마다 한 줄씩 쌓임 (수정·삭제하지 않음)
CREATE TABLE approval_history (
    hist_id     INT AUTO_INCREMENT PRIMARY KEY,
    expense_id  INT          NOT NULL,
    actor_id    INT          NOT NULL,              -- 처리한 사람
    action      VARCHAR(10)  NOT NULL,              -- 신청 / 승인 / 반려
    action_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    memo        VARCHAR(200),
    FOREIGN KEY (expense_id) REFERENCES expense(expense_id),
    FOREIGN KEY (actor_id)   REFERENCES employee(emp_id),
    CHECK (action IN ('신청','승인','반려'))
);

-- ---------- 샘플 데이터 ----------
INSERT INTO dept (dept_name) VALUES ('총무팀'), ('연구지원팀');
INSERT INTO employee (dept_id, emp_name, role) VALUES
(1, '김담당', '담당자'), (1, '박팀장', '결재자'),
(2, '이담당', '담당자'), (2, '최팀장', '결재자');
INSERT INTO budget_item (item_code, item_name) VALUES
('B100', '사무용품비'), ('B200', '출장비'), ('B300', '교육비');
INSERT INTO budget (fiscal_year, dept_id, item_id, amount) VALUES
(2026, 1, 1, 3000000), (2026, 1, 2, 5000000),
(2026, 2, 2, 8000000), (2026, 2, 3, 4000000);

-- ---------- 업무 SQL ----------
-- [잔액 조회] 편성액 - (승인 + 대기 중인 신청액)  ※ 대기 중인 금액도 미리 잡아둬야 초과 신청을 막을 수 있음
SELECT b.budget_id, d.dept_name, i.item_name, b.amount AS 편성액,
       COALESCE(SUM(CASE WHEN e.status IN ('승인','대기') THEN e.amount END), 0) AS 사용예정액,
       b.amount - COALESCE(SUM(CASE WHEN e.status IN ('승인','대기') THEN e.amount END), 0) AS 잔액
FROM budget b
JOIN dept d        ON d.dept_id = b.dept_id
JOIN budget_item i ON i.item_id = b.item_id
LEFT JOIN expense e ON e.budget_id = b.budget_id
WHERE b.fiscal_year = 2026
GROUP BY b.budget_id, d.dept_name, i.item_name, b.amount;

-- [집행 신청] 잔액 안에서만 등록 + 결재 이력에 '신청' 기록 (한 트랜잭션)
--   실제 프로그램에서는 아래 SELECT로 잔액을 확인한 뒤, 부족하면 INSERT하지 않고 오류 메시지를 띄움
START TRANSACTION;
SELECT b.amount - COALESCE(SUM(CASE WHEN e.status IN ('승인','대기') THEN e.amount END), 0) AS 잔액
FROM budget b LEFT JOIN expense e ON e.budget_id = b.budget_id
WHERE b.budget_id = 1 GROUP BY b.amount FOR UPDATE;
INSERT INTO expense (budget_id, emp_id, title, amount, req_date) VALUES (1, 1, '복사용지 구매', 500000, '2026-10-20');
INSERT INTO approval_history (expense_id, actor_id, action) VALUES (LAST_INSERT_ID(), 1, '신청');
COMMIT;

START TRANSACTION;
INSERT INTO expense (budget_id, emp_id, title, amount, req_date) VALUES (3, 3, '세미나 출장', 1200000, '2026-10-20');
INSERT INTO approval_history (expense_id, actor_id, action) VALUES (LAST_INSERT_ID(), 3, '신청');
COMMIT;

-- [승인] 결재자가 1번 신청을 승인: 상태 변경 + 이력 추가 (대기 상태일 때만)
START TRANSACTION;
UPDATE expense SET status = '승인' WHERE expense_id = 1 AND status = '대기';
INSERT INTO approval_history (expense_id, actor_id, action, memo) VALUES (1, 2, '승인', '확인했습니다');
COMMIT;

-- [반려] 2번 신청 반려
START TRANSACTION;
UPDATE expense SET status = '반려' WHERE expense_id = 2 AND status = '대기';
INSERT INTO approval_history (expense_id, actor_id, action, memo) VALUES (2, 4, '반려', '증빙 서류 보완 필요');
COMMIT;

-- [결재 이력 조회] 누가 언제 무엇을 했는지
SELECT e.expense_id, e.title, h.action, emp.emp_name AS 처리자, h.action_at, h.memo
FROM approval_history h
JOIN expense e    ON e.expense_id = h.expense_id
JOIN employee emp ON emp.emp_id   = h.actor_id
ORDER BY e.expense_id, h.hist_id;

-- [부서별 집행률] 승인된 금액 기준
SELECT d.dept_name, SUM(b.amount) AS 편성액,
       COALESCE(SUM(x.approved), 0) AS 집행액,
       ROUND(COALESCE(SUM(x.approved), 0) / SUM(b.amount) * 100, 1) AS 집행률
FROM budget b
JOIN dept d ON d.dept_id = b.dept_id
LEFT JOIN (SELECT budget_id, SUM(amount) AS approved
           FROM expense WHERE status = '승인' GROUP BY budget_id) x ON x.budget_id = b.budget_id
WHERE b.fiscal_year = 2026
GROUP BY d.dept_id, d.dept_name;
