------------------------------------------------------------------------------------------------
-- MariaDB(MySQL)向け
------------------------------------------------------------------------------------------------

-- テーブル削除
-- drop table stock_master;
-- drop table stock_type_master;
-- drop table stock_type_data;
-- drop table auth_master;
-- drop table staff_data;
-- drop table stock_data;

-- 項目名変更
-- ALTER TABLE stock_master RENAME COLUMN type TO stock_type; 

-- テーブル作成
create table stock_master( 
    stock_code varchar (20) NOT NULL PRIMARY KEY comment "シリアルナンバー"
    , name varchar (100) comment "備品名称"
    , model varchar (100) comment "型番"
    , maker varchar (100) comment "メーカー"
    , stock_type int comment "分類コード"
    , rent_flg boolean comment "貸出可否"
    , remarks varchar (4000) comment "備考"
    , del boolean comment "削除フラグ"
) comment "備品マスター"; 

create table stock_type_master( 
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT comment "採番"
    , stock_type int comment "分類コード"
    , name varchar (100) comment "分類名称"
    , del boolean comment "削除フラグ"
) comment "備品分類マスター"; 

create table stock_type_data( 
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT comment "採番"
    , stock_type int comment "分類コード"
    , auth_no int comment "権限"
    , read_flg boolean comment "読み込み可否"
    , write_flg boolean comment "書き込み可否"
    , del_flg boolean comment "削除可否"
    , del boolean comment "削除フラグ"
) comment "備品分類データ"; 

create table auth_master( 
    auth_no int NOT NULL PRIMARY KEY comment "権限"
    , auth_name varchar (20) comment "権限の名称"
    , remarks varchar (4000) comment "備考"
    , del boolean comment "削除フラグ"
) comment "権限マスター"; 

create table staff_data( 
    staff_no int NOT NULL PRIMARY KEY comment "社員番号"
    , name varchar (40) comment "氏名"
    , auth_no int comment "権限"
    , del boolean comment "削除フラグ"
) comment "社員データ"; 

create table stock_data( 
    id int NOT NULL PRIMARY KEY AUTO_INCREMENT comment "採番"
    , stock_code varchar (20) NOT NULL comment "シリアルナンバー"
    , parent_stock_code varchar (20) comment "親シリアルナンバー"
    , staff_no int comment "貸出先ユーザーの社員番号"
    , start_date date comment "貸出開始日"
    , limit_date date comment "返却予定日"
    , return_date date comment "返却日"
    , confirmed_date date comment "最終所在確認日"
    , del boolean NOT NULL default '0' comment "削除フラグ"
) comment "備品データ";

