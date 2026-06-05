using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace AssetManagementPassKeyLogIn.Entities
{
    /// <summary>
    /// DBテーブル [staff_passkeys]:社員パスキーデータ エンティティ(MODEL)
    /// </summary>
    [Table("staff_passkeys")] 
    public class StaffPasskey
    {
        /// <summary>
        /// キー識別ID（主キー）
        /// </summary>
        /// <remarks> MySQLの主キー制限エラーを回避するため、上限サイズを指定 </remarks>
        [Key]
        [Column("credential_id")]
        [MaxLength(512)]
        public byte[] CredentialId { get; set; } = null!;

        /// <summary>
        /// 社員番号（外部キー）
        /// </summary>
        [Column("staff_no")]
        public int StaffNo { get; set; }

        /// <summary>
        /// デバイス名
        /// </summary>
        /// <remarks> 例: iPhone, Windows Hello </remarks>
        [Column("friendly_name")]
        [MaxLength(100)]
        public string? FriendlyName { get; set; }

        /// <summary>
        /// 公開鍵データ
        /// </summary>
        /// <remarks> DB型[blob]：Binary Large Object(最大:64kByte) </remarks>
        [Column("public_key")]
        public byte[] PublicKey { get; set; } = null!;

        /// <summary>
        /// 署名カウンタ（クローン検知用）
        /// </summary>
        [Column("signature_count")]
        public int SignatureCount { get; set; }

        /// <summary>
        /// 
        /// </summary>
        [Column("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.Now;
    }
}
