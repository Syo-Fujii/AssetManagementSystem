package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.dbTablesModel.GenericCodeMasterModel;

/**
 * マスタ：汎用マスタ DB操作メソッド(Mapper)
 */
public interface GenericCodeMasterMapper {
    
	/**
	 * 全件取得
	 * @return　汎用マスタ(ConboBox等に表示するデータ)
	 * @brief ConboBoxの選択肢一覧取得の際などに用いるデータ<br>
	 */
    List<GenericCodeMasterModel> selectAll();
  
	/**
	 * 汎用マスタの取得(ComboBox用)
	 * @return 汎用マスターの一覧
	 * @brief 引数を指定(null以外)した場合、検索条件に含める<br>
	 */
    List<GenericCodeMasterModel> genericCodeMasterData(
    		@Param("genericKey") String genericKey,
    		@Param("type") String type,
    		@Param("code") String code,
    		@Param("isActive") Boolean isActive);	    		
}
