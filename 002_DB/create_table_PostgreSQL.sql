------------------------------------------------------------------------------------------------
-- PostgreSQL向け
------------------------------------------------------------------------------------------------

-- テーブル削除
-- drop table stock_master;
-- drop table stock_type_master;
-- drop table stock_type_data;
-- drop table auth_master;
-- drop table staff_data;
-- drop table stock_data;

-- テーブル作成
create table stock_master ( 
    stock_code varchar (20) NOT NULL PRIMARY KEY 
    , name varchar (100) 
    , model varchar (100) 
    , maker varchar (100) 
    , stock_type int 
    , rent_flg boolean 
    , remarks varchar (4000) 
    , del boolean 
) ; 

COMMENT ON TABLE stock_master IS '備品マスター';
COMMENT ON COLUMN stock_master.stock_code IS 'シリアルナンバー';
COMMENT ON COLUMN stock_master.name IS '備品名称';
COMMENT ON COLUMN stock_master.model IS '型番';
COMMENT ON COLUMN stock_master.maker IS 'メーカー';
COMMENT ON COLUMN stock_master.stock_type IS '分類コード';
COMMENT ON COLUMN stock_master.rent_flg IS '貸出可否';
COMMENT ON COLUMN stock_master.remarks IS '備考';
COMMENT ON COLUMN stock_master.del IS '削除フラグ';

create table stock_type_master( 
    id SERIAL NOT NULL PRIMARY KEY 
    , stock_type int 
    , name varchar (100) 
    , del boolean 
) ; 

COMMENT ON TABLE stock_type_master IS '備品分類マスター';
COMMENT ON COLUMN stock_type_master.id IS '採番';
COMMENT ON COLUMN stock_type_master.stock_type IS '分類コード';
COMMENT ON COLUMN stock_type_master.name IS '分類名称';
COMMENT ON COLUMN stock_type_master.del IS '削除フラグ';

create table stock_type_data( 
    id SERIAL NOT NULL PRIMARY KEY 
    , stock_type int 
    , auth_no int 
    , read_flg boolean 
    , write_flg boolean 
    , del_flg boolean 
    , del boolean 
) ; 

COMMENT ON TABLE stock_type_data IS '備品分類データ';
COMMENT ON COLUMN stock_type_data.id IS '採番';
COMMENT ON COLUMN stock_type_data.stock_type IS '分類コード';
COMMENT ON COLUMN stock_type_data.auth_no IS '権限';
COMMENT ON COLUMN stock_type_data.read_flg IS '読み込み可否';
COMMENT ON COLUMN stock_type_data.write_flg IS '書き込み可否';
COMMENT ON COLUMN stock_type_data.del_flg IS '削除可否';
COMMENT ON COLUMN stock_type_data.del IS '削除フラグ';

create table auth_master( 
    auth_no int NOT NULL PRIMARY KEY 
    , auth_name varchar (20) 
    , remarks varchar (4000) 
    , del boolean 
) ; 

COMMENT ON TABLE auth_master IS '権限マスター';
COMMENT ON COLUMN auth_master.auth_no IS '権限';
COMMENT ON COLUMN auth_master.auth_name IS '権限の名称';
COMMENT ON COLUMN auth_master.remarks IS '備考';
COMMENT ON COLUMN auth_master.del IS '削除フラグ';

create table staff_data( 
    staff_no int NOT NULL PRIMARY KEY 
    , name varchar (40) 
    , auth_no int 
    , del boolean 
) ; 

COMMENT ON TABLE staff_data IS '社員データ';
COMMENT ON COLUMN staff_data.staff_no IS '社員番号';
COMMENT ON COLUMN staff_data.name IS '氏名';
COMMENT ON COLUMN staff_data.auth_no IS '権限';
COMMENT ON COLUMN staff_data.del IS '削除フラグ';

create table stock_data( 
    id SERIAL NOT NULL PRIMARY KEY 
    , stock_code varchar (20) NOT NULL 
    , parent_stock_code varchar (20) 
    , staff_no int 
    , start_date date 
    , limit_date date 
    , return_date date 
    , confirmed_date date 
    , del boolean NOT NULL default '0' 
) ;

COMMENT ON TABLE stock_data IS '備品データ';
COMMENT ON COLUMN stock_data.id IS '採番';
COMMENT ON COLUMN stock_data.stock_code IS 'シリアルナンバー';
COMMENT ON COLUMN stock_data.parent_stock_code IS '親シリアルナンバー';
COMMENT ON COLUMN stock_data.staff_no IS '貸出先ユーザーの社員番号';
COMMENT ON COLUMN stock_data.start_date IS '貸出開始日';
COMMENT ON COLUMN stock_data.limit_date IS '返却予定日';
COMMENT ON COLUMN stock_data.return_date IS '返却日';
COMMENT ON COLUMN stock_data.confirmed_date IS '最終所在確認日';
COMMENT ON COLUMN stock_data.del IS '削除フラグ';
