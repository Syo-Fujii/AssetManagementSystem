package application.java.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseTableViewModel {

	/**
	 * 対象とするMODELの指定したプロパティの値を取得する
	 * @param propertyName String 対象プロパティ名
	 * @param type プロパティの型
	 * @return 取得した値
	 * @throws Exception
	 */
	public <R > R getModelProperty(String propertyName, Class<R> type) {
		// モデルへの値取得
		try {
	    	// メソッド(値のGetter プロパティ)名の生成
	        String methodName = "get" +
	    	                    propertyName.substring(0, 1).toUpperCase() +
	    	                    propertyName.substring(1);
	        
            // モデルからメソッドを探して実行
            Method getter = this.getClass().getMethod(methodName);
            return type.cast(getter.invoke(this));

	    } catch (InvocationTargetException itex) {
	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
            System.err.println("モデルへの値取得に失敗しました: " + propertyName);
            // リフレクション先の例外を処理
            Throwable cause = itex.getCause();
            System.err.println("メソッド内部でエラーが発生しました: " + cause.getMessage());
            cause.printStackTrace(); 
            throw new RuntimeException(cause);

	    } catch (NoSuchMethodException e) {
	    	System.err.println("モデルへの値取得に失敗しました: " + propertyName);
	    	System.err.println("メソッドが見つかりません: " + propertyName);
	        throw new RuntimeException(e);

	    } catch (Exception e) {
	    	System.err.println("モデルへの値取得に失敗しました: " + propertyName);
	    	throw new RuntimeException(e);
	    }
	}	
	
	/**
	 * 対象とするMODELの指定したプロパティに値を設定する
	 * @param propertyName String 対象プロパティ名
	 * @param type プロパティの型
	 * @param value 設定する値
	 * @throws Exception
	 */
	public void setModelProperty(String propertyName, Class<?> type, Object value ) {
		// モデルへの値反映
		try {
	    	// メソッド(値のSetter プロパティ)名の生成
	        String methodName = "set" +
	    	                    propertyName.substring(0, 1).toUpperCase() +
	    	                    propertyName.substring(1);
	        
            // モデルからメソッドを探して実行
            Method setter = this.getClass().getMethod(methodName, type);
            setter.invoke(this, type.cast(value));

	    } catch (InvocationTargetException itex) {
	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
            System.err.println("モデルへの値反映に失敗しました: " + propertyName);
            // リフレクション先の例外を処理
            Throwable cause = itex.getCause();
            System.err.println("メソッド内部でエラーが発生しました: " + cause.getMessage());
            cause.printStackTrace(); 
            throw new RuntimeException(cause);

	    } catch (NoSuchMethodException e) {
	    	System.err.println("モデルへの値反映に失敗しました: " + propertyName);
	    	System.err.println("メソッドが見つかりません: " + propertyName);
	        throw new RuntimeException(e);

	    } catch (Exception e) {
	    	System.err.println("モデルへの値反映に失敗しました: " + propertyName);
	    	throw new RuntimeException(e);
	    }
	}
}
