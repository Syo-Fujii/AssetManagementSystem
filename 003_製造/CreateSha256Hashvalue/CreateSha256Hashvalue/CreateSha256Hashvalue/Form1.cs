using System.Security.Cryptography;
using System.Windows.Forms.VisualStyles;
using TextBox = System.Windows.Forms.TextBox;

namespace CreateSha256Hashvalue
{
    /// <summary>
    /// /
    /// </summary>
    public partial class Form1 : Form
    {
        private const string ITERATION_DEFULT_VALUE = "60000";
        private const string HASH_BYTE_DEFULT_VALUE = "32";
        private const string SALT_BYTE_DEFULT_VALUE = "16";

        /// <summary>
        /// コンストラクタ
        /// </summary>
        public Form1()
        {
            InitializeComponent();

            this.txtIterations.Tag = ITERATION_DEFULT_VALUE;
            this.txtHashBtye.Tag = HASH_BYTE_DEFULT_VALUE;
            this.txtSaltByte.Tag = SALT_BYTE_DEFULT_VALUE;
        }

        /// <summary>
        /// 設定値:Key押下イベント
        /// </summary>
        /// <remarks>
        /// 「半角の数字」のみKey入力を許可する
        /// </remarks>
        private void SettingsTextBox_KeyPress(object sender, KeyPressEventArgs e)
        {
            // e.KeyChar が '0'～'9' の半角数字、またはバックスペース（'\b'）でなければ入力を無効化
            if ((e.KeyChar < '0' || e.KeyChar > '9') && e.KeyChar != '\b')
            {
                e.Handled = true; // 入力をキャンセルする
            }
        }

        /// <summary>
        /// 設定値:値変更イベント
        /// </summary>
        /// <remarks>
        /// 「半角の数字」のみ値変更を許可する
        /// </remarks>
        private void SettingsTextBox_TextChanged(object sender, EventArgs e)
        {
            // 入力された文字列に、半角数字以外の文字が含まれているかチェック
            var ctrl = (TextBox)sender;
            
            foreach (char c in ctrl.Text)
            {
                if (c < '0' || c > '9')
                {
                    // 数字以外の文字（全角やアルファベット）が含まれていたら、強制的にクリアするか警告する
                    MessageBox.Show("設定値には半角数字のみを入力してください。", "入力エラー", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    ctrl.Text = ctrl.Tag?.ToString() ?? "0";

                    return;
                }
            }
        }

        /// <summary>
        /// 設定値:変更チェックイベント
        /// </summary>
        private void checkBox1_CheckedChanged(object sender, EventArgs e)
        {
            var isChecked = ((CheckBox)sender).Checked;

            this.gbSettings.Enabled = isChecked;
        }

        /// <summary>
        /// 変換文字（パスワード）のテキストボックスに「半角英数字・記号」のみ入力を許可するイベント
        /// </summary>
        private void txtConvertWords_KeyPress(object sender, KeyPressEventArgs e)
        {
            // 制御文字（バックスペースや、Ctrl+C/Ctrl+Vなどのショートカット）は許可する
            if (char.IsControl(e.KeyChar))
            {
                return;
            }

            // ASCII文字（半角英数字・記号）の範囲のみ許可する（32～126）
            // ※半角スペース（32）を禁止したい場合は「e.KeyChar == ' '」を条件に加えて弾きます
            if (e.KeyChar >= 32 && e.KeyChar <= 126)
            {
                // 許可された半角文字なので、何もしない（入力を通す）
                return;
            }

            // 上記以外の文字（全角文字、全角スペースなど）はすべて入力をキャンセル
            e.Handled = true;
        }

        /// <summary>
        /// 変換文字:値変更イベント
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void txtConvertWords_TextChanged(object sender, EventArgs e)
        {
            var ctrl = ((TextBox)sender);
            string input = ctrl.Text;

            // 入力された文字に半角（ASCII）以外の文字が混ざっていないか検証
            foreach (char c in input)
            {
                if (c < 32 || c > 126)
                {
                    MessageBox.Show("パスワードには半角の英数字・記号のみを使用してください。", "入力エラー", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    ctrl.Clear(); // 不正な文字があれば強制クリア
                    return;
                }
            }
        }

        /// <summary>
        /// HASH値生成ボタン押下
        /// </summary>
        /// <param name="sender"> ボタン</param>
        /// <param name="e"> EventArgs </param>
        private void btnGenerateHash_Click(object sender, EventArgs e)
        {
            var iterationsText = this.txtIterations.Text.Trim();
            var hashByteText = this.txtHashBtye.Text.Trim();
            var saltByteText = this.txtSaltByte.Text.Trim();
            
            if (string.IsNullOrWhiteSpace(iterationsText) || 
                string.IsNullOrWhiteSpace(hashByteText) || 
                string.IsNullOrWhiteSpace(saltByteText))
            {
                MessageBox.Show("設定値を入力してください。", "入力エラー", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            var words = this.txtConvertWords.Text.Trim();

            if (string.IsNullOrWhiteSpace(words))
            {
                MessageBox.Show("変換文字を入力してください。", "入力エラー", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }

            try
            {
                var iterationi = Convert.ToInt32(iterationsText);
                var salt = Convert.ToInt32(saltByteText);
                var hash = Convert.ToInt32(hashByteText);

                // 暗号学的に安全なランダムな「ソルト」を毎回生成
                byte[] saltBytes = RandomNumberGenerator.GetBytes(salt);

                // PBKDF2-SHA256 でハッシュ値を計算
                byte[] hashBytes = Rfc2898DeriveBytes.Pbkdf2(
                    words,                      // 平文パスワード（string）
                    saltBytes,                  // 生成したソルト（byte[]）
                    iterationi,                 // ストレッチング回数（int）
                    HashAlgorithmName.SHA256,   // ハッシュアルゴリズム
                    hash                        // 出力するハッシュのバイト長（int）
                    );

                // それぞれをBase64形式の文字列に変換
                var saltBase64 = Convert.ToBase64String(saltBytes);
                var hashBase64 = Convert.ToBase64String(hashBytes);

                // 既存DBの1カラムに詰め込めるよう「$」で連結
                // 形式: $アルゴリズム$ストレッチング回数$ソルト$ハッシュ値
                var dbSaveFormat = $"$pbkdf2-sha256${iterationi}${saltBase64}${hashBase64}";

                // 5. 画面の出力用テキストボックスに表示
                this.txtHashValue.Text = dbSaveFormat;

            }
            catch (Exception ex)
            {
                MessageBox.Show($"生成中にエラーが発生しました:\n{ex.Message}", "エラー", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }
    }
}
