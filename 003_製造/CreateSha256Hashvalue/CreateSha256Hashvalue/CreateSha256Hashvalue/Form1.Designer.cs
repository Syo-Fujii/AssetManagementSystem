namespace CreateSha256Hashvalue
{
    partial class Form1
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            gbSettings = new GroupBox();
            txtSaltByte = new TextBox();
            txtHashBtye = new TextBox();
            txtIterations = new TextBox();
            label3 = new Label();
            label2 = new Label();
            label1 = new Label();
            txtConvertWords = new TextBox();
            groupBox2 = new GroupBox();
            groupBox3 = new GroupBox();
            btnGenerateHash = new Button();
            txtHashValue = new TextBox();
            chkSettings = new CheckBox();
            gbSettings.SuspendLayout();
            groupBox2.SuspendLayout();
            groupBox3.SuspendLayout();
            SuspendLayout();
            // 
            // gbSettings
            // 
            gbSettings.Controls.Add(txtSaltByte);
            gbSettings.Controls.Add(txtHashBtye);
            gbSettings.Controls.Add(txtIterations);
            gbSettings.Controls.Add(label3);
            gbSettings.Controls.Add(label2);
            gbSettings.Controls.Add(label1);
            gbSettings.Enabled = false;
            gbSettings.Location = new Point(27, 27);
            gbSettings.Name = "gbSettings";
            gbSettings.Size = new Size(311, 189);
            gbSettings.TabIndex = 0;
            gbSettings.TabStop = false;
            gbSettings.Text = "設定値";
            // 
            // txtSaltByte
            // 
            txtSaltByte.ImeMode = ImeMode.Disable;
            txtSaltByte.Location = new Point(177, 140);
            txtSaltByte.Name = "txtSaltByte";
            txtSaltByte.Size = new Size(87, 31);
            txtSaltByte.TabIndex = 5;
            txtSaltByte.Text = "16";
            txtSaltByte.TextChanged += SettingsTextBox_TextChanged;
            txtSaltByte.KeyPress += SettingsTextBox_KeyPress;
            // 
            // txtHashBtye
            // 
            txtHashBtye.ImeMode = ImeMode.Disable;
            txtHashBtye.Location = new Point(177, 87);
            txtHashBtye.Name = "txtHashBtye";
            txtHashBtye.Size = new Size(87, 31);
            txtHashBtye.TabIndex = 4;
            txtHashBtye.Text = "32";
            txtHashBtye.TextChanged += SettingsTextBox_TextChanged;
            txtHashBtye.KeyPress += SettingsTextBox_KeyPress;
            // 
            // txtIterations
            // 
            txtIterations.ImeMode = ImeMode.Disable;
            txtIterations.Location = new Point(177, 39);
            txtIterations.Name = "txtIterations";
            txtIterations.Size = new Size(87, 31);
            txtIterations.TabIndex = 3;
            txtIterations.Text = "60000";
            txtIterations.TextChanged += SettingsTextBox_TextChanged;
            txtIterations.KeyPress += SettingsTextBox_KeyPress;
            // 
            // label3
            // 
            label3.AutoSize = true;
            label3.Location = new Point(31, 140);
            label3.Name = "label3";
            label3.Size = new Size(112, 25);
            label3.TabIndex = 2;
            label3.Text = "SALT長(Byte)";
            // 
            // label2
            // 
            label2.AutoSize = true;
            label2.Location = new Point(31, 90);
            label2.Name = "label2";
            label2.Size = new Size(122, 25);
            label2.TabIndex = 1;
            label2.Text = "HASH長(Byte)";
            // 
            // label1
            // 
            label1.AutoSize = true;
            label1.Location = new Point(31, 42);
            label1.Name = "label1";
            label1.Size = new Size(140, 25);
            label1.TabIndex = 0;
            label1.Text = "ストレッチング回数";
            // 
            // txtConvertWords
            // 
            txtConvertWords.ImeMode = ImeMode.Disable;
            txtConvertWords.Location = new Point(20, 41);
            txtConvertWords.Name = "txtConvertWords";
            txtConvertWords.Size = new Size(697, 31);
            txtConvertWords.TabIndex = 7;
            txtConvertWords.TextChanged += txtConvertWords_TextChanged;
            txtConvertWords.KeyPress += txtConvertWords_KeyPress;
            // 
            // groupBox2
            // 
            groupBox2.Controls.Add(txtConvertWords);
            groupBox2.Location = new Point(27, 234);
            groupBox2.Name = "groupBox2";
            groupBox2.Size = new Size(749, 92);
            groupBox2.TabIndex = 8;
            groupBox2.TabStop = false;
            groupBox2.Text = "変換文字";
            // 
            // groupBox3
            // 
            groupBox3.Controls.Add(btnGenerateHash);
            groupBox3.Controls.Add(txtHashValue);
            groupBox3.Location = new Point(27, 356);
            groupBox3.Name = "groupBox3";
            groupBox3.Size = new Size(749, 211);
            groupBox3.TabIndex = 9;
            groupBox3.TabStop = false;
            groupBox3.Text = "HASH値";
            // 
            // btnGenerateHash
            // 
            btnGenerateHash.Location = new Point(20, 42);
            btnGenerateHash.Name = "btnGenerateHash";
            btnGenerateHash.Size = new Size(151, 34);
            btnGenerateHash.TabIndex = 8;
            btnGenerateHash.Text = "HASH値 生成";
            btnGenerateHash.UseVisualStyleBackColor = true;
            btnGenerateHash.Click += btnGenerateHash_Click;
            // 
            // txtHashValue
            // 
            txtHashValue.Location = new Point(20, 95);
            txtHashValue.Multiline = true;
            txtHashValue.Name = "txtHashValue";
            txtHashValue.ReadOnly = true;
            txtHashValue.ScrollBars = ScrollBars.Vertical;
            txtHashValue.Size = new Size(708, 100);
            txtHashValue.TabIndex = 7;
            // 
            // chkSettings
            // 
            chkSettings.AutoSize = true;
            chkSettings.Location = new Point(356, 45);
            chkSettings.Name = "chkSettings";
            chkSettings.Size = new Size(128, 29);
            chkSettings.TabIndex = 10;
            chkSettings.Text = "設定値変更";
            chkSettings.UseVisualStyleBackColor = true;
            chkSettings.CheckedChanged += checkBox1_CheckedChanged;
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(10F, 25F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(800, 605);
            Controls.Add(chkSettings);
            Controls.Add(groupBox3);
            Controls.Add(groupBox2);
            Controls.Add(gbSettings);
            Name = "Form1";
            Text = "ハッシュ値(SHA256 - PBKDF2)生成システム";
            Load += Form1_Load;
            gbSettings.ResumeLayout(false);
            gbSettings.PerformLayout();
            groupBox2.ResumeLayout(false);
            groupBox2.PerformLayout();
            groupBox3.ResumeLayout(false);
            groupBox3.PerformLayout();
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private GroupBox gbSettings;
        private Label label3;
        private Label label2;
        private Label label1;
        private TextBox txtIterations;
        private TextBox txtHashBtye;
        private TextBox txtSaltByte;
        private TextBox txtConvertWords;
        private GroupBox groupBox2;
        private GroupBox groupBox3;
        private TextBox txtHashValue;
        private CheckBox chkSettings;
        private Button btnGenerateHash;
    }
}
