/**
 * パスキー（WebAuthn API）呼び出し用JS
 * @param {any} options
 * @returns
 * パスキー（指紋・顔認証）を呼び出すためのスクリプト
 * ログイン画面（Login.razor / Blazor）から呼び出す
 */

/**
 * 【新規登録用】新しいパスキーをデバイス（スマホ等）に生成する
 */
export async function createCredential(options) {
    // サーバーから届いたBase64データをバイナリ(ArrayBuffer)に変換
    options.publicKey.challenge = coerceToArrayBuffer(options.publicKey.challenge);
    options.publicKey.user.id = coerceToArrayBuffer(options.publicKey.user.id);

    if (options.publicKey.excludeCredentials) {
        options.publicKey.excludeCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // ブラウザの認証ダイアログを起動
    const credential = await navigator.credentials.create(options);

    // 認証結果をサーバーが読める形にシリアライズして返す
    return serializeCredential(credential);
}

/**
 * 【通常ログイン用】既存のパスキー（ポップアップダイアログ形式）を使ってログインする
 */
export async function getAssertion(options) {
    options.publicKey.challenge = coerceToArrayBuffer(options.publicKey.challenge);

    if (options.publicKey.allowCredentials) {
        options.publicKey.allowCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // 既存のパスキーを使ってログイン
    const credential = await navigator.credentials.get(options);

    return serializeCredential(credential);
}

/**
 * 【QRコードログイン用】画面上のQRコードの読み取りをバックグラウンドで待ち受ける
 * ※ Login.razor.cs の「module.InvokeAsync("getAssertionConditional", ... )」から呼び出し
 */
export async function getAssertionConditional(options) {
    // 1. サーバーから届いたBase64データをバイナリに変換
    options.publicKey.challenge = coerceToArrayBuffer(options.publicKey.challenge);

    if (options.publicKey.allowCredentials) {
        options.publicKey.allowCredentials.forEach(c => c.id = coerceToArrayBuffer(c.id));
    }

    // ブラウザの強制ポップアップを抑え、画面裏（バックグラウンド）でスマホを待ち受ける設定
    options.mediation = 'conditional';

    // ブラウザのWebAuthnAPIを起動（スマホでQRがスキャンされて生体認証が通るまでここで非同期待機）
    const credential = await navigator.credentials.get(options);

    // 認証結果をシリアライズしてBlazor（C#）側へ連れて帰る
    return serializeCredential(credential);
}


// 補助関数：Base64URLをArrayBufferに変換
function coerceToArrayBuffer(data) {
    if (typeof data === 'string') {
        const bin = atob(data.replace(/-/g, '+').replace(/_/g, '/'));

        return Uint8Array.from(bin, c => c.charCodeAt(0)).buffer;
    }

    return data;
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
