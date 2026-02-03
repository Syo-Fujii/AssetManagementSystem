------------------------------------------------------------------------------------------------
-- PostgreSQL向け
------------------------------------------------------------------------------------------------
-- drop view equip_list_view;

-- 備品一覧参照ビュー
CREATE 
OR REPLACE view equip_list_view AS WITH equip_work AS ( 
    SELECT
        stock_type_master.name AS lending_name  -- 貸出備品の名称
        , COUNT(stock_master.stock_type) AS lending_count -- 貸出備品の数
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = false 
        AND stock_data.staff_no is not null 
        AND stock_data.return_date is null 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, stock_work AS ( 
    SELECT
        stock_type_master.name AS stock_name    -- 在庫備品の名称
        , COUNT(stock_master.stock_type) AS stock_count -- 在庫備品の数
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = false 
        AND stock_data.staff_no is null 
        AND stock_data.return_date is null 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, unknown_work AS ( 
    SELECT
        stock_type_master.name AS unknown_name  -- 不明備品の名称
        , COUNT(stock_master.stock_type) AS unknown_count -- 不明備品の数
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
    WHERE
        stock_master.del = false 
        AND CURRENT_DATE - stock_data.confirmed_date > 365 
    GROUP BY
        stock_type_master.name
        , stock_master.stock_type
) 
, aggregate_work AS ( 
    -- 集計
    SELECT
        stock_type_master.id
        , stock_type_master.name AS 名称
        , CASE 
            WHEN equip_work.lending_count IS NULL 
                THEN 0 
            ELSE equip_work.lending_count 
            END AS 貸出
        , CASE 
            WHEN stock_work.stock_count IS NULL 
                THEN 0 
            ELSE stock_work.stock_count 
            END AS 在庫
        , CASE 
            WHEN unknown_work.unknown_count IS NULL 
                THEN 0 
            ELSE unknown_work.unknown_count 
            END AS 不明
        , COUNT(stock_master.stock_type) AS 合計 
    FROM
        stock_data 
        INNER JOIN stock_master 
            ON stock_data.stock_code = stock_master.stock_code 
        INNER JOIN stock_type_master 
            ON stock_master.stock_type = stock_type_master.stock_type 
        LEFT JOIN equip_work 
            ON equip_work.lending_name = stock_type_master.name 
        LEFT JOIN stock_work 
            ON stock_work.stock_name = stock_type_master.name 
        LEFT JOIN unknown_work 
            ON unknown_work.unknown_name = stock_type_master.name 
    WHERE
        stock_master.del = false 
        AND stock_data.return_date is null 
    GROUP BY
        equip_work.lending_count
        , stock_work.stock_count
        , unknown_work.unknown_count
        , stock_type_master.id
        , stock_type_master.name
        , stock_master.stock_type 
    ORDER BY
        stock_type_master.id
) 
SELECT
    名称
    , 貸出
    , 在庫
    , 不明
    , 合計 
FROM
    aggregate_work;


-- 備品詳細参照ビュー
CREATE 
OR REPLACE view equip_detail_view AS SELECT DISTINCT
    stock_type_master.name AS 備品名
    , stock_data.stock_code AS シリアルナンバー
    , staff_data.name AS 使用者
    , case 
        when CURRENT_DATE - stock_data.confirmed_date > 365 
            then '不明' 
        when stock_master.rent_flg = true
            then '不可' 
        when staff_data.name is null 
            then '可' 
        when staff_data.name is not null 
            then '貸出中' 
        else  '不可' 
        end AS 貸出可否
    , stock_data.start_date AS 貸出開始日
    , stock_data.limit_date AS 返却予定日
    , stock_data.confirmed_date AS 最終所在確認日
    , stock_master.remarks AS 備考 
    , stock_data.id AS 備品データID
    , staff_data.staff_no AS 社員番号
FROM
    stock_type_master 
    INNER JOIN stock_type_data 
        ON stock_type_master.stock_type = stock_type_data.stock_type 
    INNER JOIN stock_master 
        ON stock_type_master.stock_type = stock_master.stock_type 
    LEFT JOIN stock_data 
        ON stock_master.stock_code = stock_data.stock_code 
    LEFT JOIN staff_data 
        ON stock_data.staff_no = staff_data.staff_no 
WHERE
    stock_master.del = false and stock_data.del = false and stock_data.return_date is null
ORDER BY
stock_data.stock_code;
