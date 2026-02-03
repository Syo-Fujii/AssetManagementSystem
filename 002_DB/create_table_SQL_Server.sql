-- テーブル作成: stock_master
CREATE TABLE stock_master ( 
    stock_code VARCHAR(20) NOT NULL PRIMARY KEY
    , name VARCHAR(100)
    , model VARCHAR(100)
    , maker VARCHAR(100)
    , stock_type INT
    , rent_flg BIT
    , remarks VARCHAR(4000)
    , del BIT
);

-- テーブル作成: stock_type_master
CREATE TABLE stock_type_master ( 
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1)
    , stock_type INT
    , name VARCHAR(100)
    , del BIT
);

-- テーブル作成: stock_type_data
CREATE TABLE stock_type_data ( 
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1)
    , stock_type INT
    , auth_no INT
    , read_flg BIT
    , write_flg BIT
    , del_flg BIT
    , del BIT
);

-- テーブル作成: auth_master
CREATE TABLE auth_master ( 
    auth_no INT NOT NULL PRIMARY KEY
    , auth_name VARCHAR(20)
    , remarks VARCHAR(4000)
    , del BIT
);

-- テーブル作成: staff_data
CREATE TABLE staff_data ( 
    staff_no INT NOT NULL PRIMARY KEY
    , name VARCHAR(40)
    , auth_no INT
    , del BIT
);

-- テーブル作成: stock_data
CREATE TABLE stock_data ( 
    id INT NOT NULL PRIMARY KEY IDENTITY(1,1)
    , stock_code VARCHAR(20) NOT NULL
    , parent_stock_code VARCHAR(20)
    , staff_no INT
    , start_date DATE
    , limit_date DATE
    , return_date DATE
    , confirmed_date DATE
    , del BIT NOT NULL DEFAULT 0
);


-- *******************************************************************
-- 拡張プロパティ (コメント) の追加
-- *******************************************************************

-- stock_master
EXEC sp_addextendedproperty N'MS_Description', N'備品マスター', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'シリアルナンバー', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'stock_code';
EXEC sp_addextendedproperty N'MS_Description', N'備品名称', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'name';
EXEC sp_addextendedproperty N'MS_Description', N'型番', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'model';
EXEC sp_addextendedproperty N'MS_Description', N'メーカー', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'maker';
EXEC sp_addextendedproperty N'MS_Description', N'分類コード', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'stock_type';
EXEC sp_addextendedproperty N'MS_Description', N'貸出可否', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'rent_flg';
EXEC sp_addextendedproperty N'MS_Description', N'備考', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'remarks';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'stock_master', N'COLUMN', N'del';

-- stock_type_master
EXEC sp_addextendedproperty N'MS_Description', N'備品分類マスター', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_master', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'採番', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_master', N'COLUMN', N'id';
EXEC sp_addextendedproperty N'MS_Description', N'分類コード', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_master', N'COLUMN', N'stock_type';
EXEC sp_addextendedproperty N'MS_Description', N'分類名称', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_master', N'COLUMN', N'name';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_master', N'COLUMN', N'del';

-- stock_type_data
EXEC sp_addextendedproperty N'MS_Description', N'備品分類データ', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'採番', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'id';
EXEC sp_addextendedproperty N'MS_Description', N'分類コード', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'stock_type';
EXEC sp_addextendedproperty N'MS_Description', N'権限', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'auth_no';
EXEC sp_addextendedproperty N'MS_Description', N'読み込み可否', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'read_flg';
EXEC sp_addextendedproperty N'MS_Description', N'書き込み可否', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'write_flg';
EXEC sp_addextendedproperty N'MS_Description', N'削除可否', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'del_flg';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'stock_type_data', N'COLUMN', N'del';

-- auth_master
EXEC sp_addextendedproperty N'MS_Description', N'権限マスター', N'SCHEMA', N'dbo', N'TABLE', N'auth_master', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'権限', N'SCHEMA', N'dbo', N'TABLE', N'auth_master', N'COLUMN', N'auth_no';
EXEC sp_addextendedproperty N'MS_Description', N'権限の名称', N'SCHEMA', N'dbo', N'TABLE', N'auth_master', N'COLUMN', N'auth_name';
EXEC sp_addextendedproperty N'MS_Description', N'備考', N'SCHEMA', N'dbo', N'TABLE', N'auth_master', N'COLUMN', N'remarks';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'auth_master', N'COLUMN', N'del';

-- staff_data
EXEC sp_addextendedproperty N'MS_Description', N'社員データ', N'SCHEMA', N'dbo', N'TABLE', N'staff_data', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'社員番号', N'SCHEMA', N'dbo', N'TABLE', N'staff_data', N'COLUMN', N'staff_no';
EXEC sp_addextendedproperty N'MS_Description', N'氏名', N'SCHEMA', N'dbo', N'TABLE', N'staff_data', N'COLUMN', N'name';
EXEC sp_addextendedproperty N'MS_Description', N'権限', N'SCHEMA', N'dbo', N'TABLE', N'staff_data', N'COLUMN', N'auth_no';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'staff_data', N'COLUMN', N'del';

-- stock_data
EXEC sp_addextendedproperty N'MS_Description', N'備品データ', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', NULL, NULL;
EXEC sp_addextendedproperty N'MS_Description', N'採番', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'id';
EXEC sp_addextendedproperty N'MS_Description', N'シリアルナンバー', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'stock_code';
EXEC sp_addextendedproperty N'MS_Description', N'親シリアルナンバー', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'parent_stock_code';
EXEC sp_addextendedproperty N'MS_Description', N'貸出先ユーザーの社員番号', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'staff_no';
EXEC sp_addextendedproperty N'MS_Description', N'貸出開始日', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'start_date';
EXEC sp_addextendedproperty N'MS_Description', N'返却予定日', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'limit_date';
EXEC sp_addextendedproperty N'MS_Description', N'返却日', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'return_date';
EXEC sp_addextendedproperty N'MS_Description', N'最終所在確認日', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'confirmed_date';
EXEC sp_addextendedproperty N'MS_Description', N'削除フラグ', N'SCHEMA', N'dbo', N'TABLE', N'stock_data', N'COLUMN', N'del';
