/**
 * パスキー（WebAuthn API）呼び出し用JS
 * @param {any} options
 * @returns
 * パスキー（指紋・顔認証）を呼び出すためのスクリプト
 * 端末(スマホ)側でQRを読み込んだ場合に呼出
 * ハイブリッド認証（Bluetooth / QRコード連動機能）
 */

/**
 * 【新規登録用】新しいパスキーをデバイス（スマホ等）に生成する
 */
export async function createCredential(options) {

    let publicKeyOptions = options;

    if (options && options.publicKey)
    {
        publicKeyOptions = options.publicKey;
    }
    else if (options && options.options)
    {
        publicKeyOptions = options.options;
    }

    // 万が一、最外枠のデータがそのまま渡ってきた場合への安全策
    if (!publicKeyOptions) {
        throw new Error("C#側から渡された認証オプションデータが空（undefined）です。");
    }

    // サーバーから届いたBase64データをバイナリ(ArrayBuffer)に変換
    publicKeyOptions.challenge = coerceToArrayBuffer(publicKeyOptions.challenge);
    publicKeyOptions.user.id = coerceToArrayBuffer(publicKeyOptions.user.id);

    if (publicKeyOptions.excludeCredentials) {
        publicKeyOptions.excludeCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // ブラウザの認証ダイアログを起動
    const credential = await navigator.credentials.create({ publicKey: publicKeyOptions });

    // 認証結果をサーバーが読める形にシリアライズして返す
    // return serializeCredential(credential);
    return serializeCredentialFromBase64Uri(credential);
}

/**
 * 【通常ログイン用】既存のパスキー（ポップアップダイアログ形式）を使ってログインする
 */
export async function getAssertion(options) {

    let publicKeyOptions = options;

    if (options && options.publicKey)
    {
        publicKeyOptions = options.publicKey;
    }
    else if (options && options.options)
    {
        publicKeyOptions = options.options;
    }

    // 万が一、最外枠のデータがそのまま渡ってきた場合への安全策
    if (!publicKeyOptions) {
        throw new Error("C#側から渡された認証オプションデータが空（undefined）です。");
    }

    // サーバーから届いたBase64データをバイナリに変換
    publicKeyOptions.challenge = coerceToArrayBuffer(publicKeyOptions.challenge);

    if (publicKeyOptions.allowCredentials) {
        publicKeyOptions.allowCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // ブラウザのWebAuthnAPIを起動（スマホでQRがスキャンされて生体認証が通るまでここで非同期待機）
    const credential = await navigator.credentials.get({ publicKey: publicKeyOptions });

    // 認証結果をシリアライズ(Json化)してBlazor（C#）側へ連れて帰る
    //return serializeCredential(credential);
    return serializeCredentialFromBase64Uri(credential);
}

/**
 * 【QRコードログイン用】画面上のQRコードの読み取りをバックグラウンドで待ち受ける
 * ※ Login.razor.cs の「module.InvokeAsync("getAssertionConditional", ... )」から呼び出し
 */
export async function getAssertionConditional(options) {

    let publicKeyOptions = options;

    if (options && options.publicKey) {
        publicKeyOptions = options.publicKey;
    }
    else if (options && options.options) {
        publicKeyOptions = options.options;
    }

    // 万が一、最外枠のデータがそのまま渡ってきた場合への安全策
    if (!publicKeyOptions) {
        throw new Error("C#側から渡された認証オプションデータが空（undefined）です。");
    }



    // サーバーから届いたBase64データをバイナリに変換
    publicKeyOptions.challenge = coerceToArrayBuffer(publicKeyOptions.challenge);

    if (publicKeyOptions.allowCredentials) {
        publicKeyOptions.allowCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // ブラウザの強制ポップアップを抑え、画面裏（バックグラウンド）でスマホを待ち受ける設定
    publicKeyOptions.mediation = 'conditional';

    // ブラウザのWebAuthnAPIを起動（スマホでQRがスキャンされて生体認証が通るまでここで非同期待機）
    const credential = await navigator.credentials.get({ publicKey: publicKeyOptions });

    // 認証結果をシリアライズ(Json化)してBlazor（C#）側へ連れて帰る
    // return serializeCredential(credential);
    return serializeCredentialFromBase64Uri(credential);
}


// 補助関数：Base64URLをArrayBufferに変換
function coerceToArrayBuffer(data) {
    if (typeof data === 'string') {
        const bin = atob(data.replace(/-/g, '+').replace(/_/g, '/'));

        return Uint8Array.from(bin, c => c.charCodeAt(0)).buffer;
    }

    return data;
}

// 補助関数：ArrayBufferを最新仕様の「Base64Url」形式の文字列に変換する
function bufferToBase64Url(buffer) {
    if (!buffer) return null;
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    // 標準のBase64に変換したあと、WebAuthn仕様のBase64Url記号（-, _）に置換し、末尾の = を消去
    return btoa(binary)
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=/g, '');
}

// 補助関数：認証結果をBase64に変換してサーバーへ送れるようにする
function serializeCredential(c) {
    return {
        id: c.id,
        rawId: btoa(String.fromCharCode(...new Uint8Array(c.rawId))),
        type: c.type,
        extensionResults: c.getClientExtensionResults(),

        response: {
            attestationObject: c.response.attestationObject ? btoa(String.fromCharCode(...new Uint8Array(c.response.attestationObject))) : null,
            clientDataJSON: btoa(String.fromCharCode(...new Uint8Array(c.response.clientDataJSON))),
            authenticatorData: c.response.authenticatorData ? btoa(String.fromCharCode(...new Uint8Array(c.response.authenticatorData))) : null,
            signature: c.response.signature ? btoa(String.fromCharCode(...new Uint8Array(c.response.signature))) : null,
            userHandle: c.response.userHandle ? btoa(String.fromCharCode(...new Uint8Array(c.response.userHandle))) : null
        }
    };
}

// 補助関数：認証結果をBase64Uriに変換してサーバーへ送れるようにする
function serializeCredentialFromBase64Uri(c) {

    // rawId が文字列（c.id）と同じ形式で届くことを保証するための安全策
    let rawIdBase64Url = bufferToBase64Url(c.rawId);
    if (!rawIdBase64Url && typeof c.id === 'string') {
        rawIdBase64Url = c.id;
    }

    return {
        id: c.id,
        rawId: rawIdBase64Url,
        type: c.type,
        extensionResults: c.getClientExtensionResults(),

        response: {
            attestationObject: c.response.attestationObject ? bufferToBase64Url(c.response.attestationObject) : null,
            clientDataJSON: bufferToBase64Url(c.response.clientDataJSON),
            authenticatorData: c.response.authenticatorData ? bufferToBase64Url(c.response.authenticatorData) : null,
            signature: c.response.signature ? bufferToBase64Url(c.response.signature) : null,
            userHandle: c.response.userHandle ? bufferToBase64Url(c.response.userHandle) : null
        }
    };
}
