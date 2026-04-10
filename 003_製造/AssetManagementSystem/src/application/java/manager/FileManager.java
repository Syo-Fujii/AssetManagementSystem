package application.java.manager;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import application.java.common.AppUtil;


/**
 * ファイル操作クラス
 */
public class FileManager {

	/**
	 * パス結合
	 * @param paths String[] 結合する文字列のリスト
	 * @return String 絶対パス(パス文字は環境依存)
     * @brief 引数の文字列をPATHとして結合する。<br>
     * PATHの連結は環境依存となる(Windowsの場合,[\])
	 */
	public String pathCombine(String[] paths) {
		List<String> elements = new ArrayList<>();
		
		for (String path : paths) {
	    	String[] nodes = 
	    			Arrays.
	    			stream(path.replace("\\", "/").split("/")).
	    			filter(node -> !AppUtil.StringIsNullOrWhiteSpace(node)).
	    			toArray(String[]::new);
	    	
	    	elements.addAll(Arrays.asList(nodes));
		}
		
		Path path = elements.stream()
                .map(Path::of)
                .reduce(Path::resolve)
                .orElseThrow();

		return path.toAbsolutePath().toString();
	}
	
	/**
	 * パス結合(リソース参照用)
	 * @param paths String[] 結合する文字列のリスト
	 * @return String 結合したPATH(絶対 / 相対)
     * @brief 引数の文字列をリソース用PATH(パスの区切りは[/])として結合する。<br>
     * リストの先頭がドライブレターから始まる場合は、絶対パス<br>
     * それ以外は参照パスとして返す。
	 */
	public String resourcePathCombine(String[] paths) {
	    List<String> elements = new ArrayList<>();

	    for (String path : paths) {
	    	if (AppUtil.StringIsNullOrWhiteSpace(path)) { continue; }
	    	
	    	String[] nodes = 
	    			Arrays.
	    			stream(path.replace("\\", "/").split("/")).
	    			filter(node -> !AppUtil.StringIsNullOrWhiteSpace(node)).
	    			toArray(String[]::new);
	    	
	    	elements.addAll(Arrays.asList(nodes));
	    }
	    
	    String result = String.join("/", elements);
	    
	    if (Paths.get(result).isAbsolute()) { return result; }
	    
	    return "/" + result; 
	}

	/**
	 * パス結合(リソース(URI)参照用)
	 * @param paths String[] 結合する文字列のリスト
	 * @return String 結合したPATH(絶対 / 相対)
     * @brief 引数の文字列をリソース用PATH(パスの区切りは[/])として結合する。<br>
     * リストの先頭がドライブレターから始まる場合は、URI(file:/)を付与した絶対パス<br>
     * それ以外は参照パスとして返す。
	 */
	public String urlPathCombine(String[] paths) {
	    List<String> elements = new ArrayList<>();

	    for (String path : paths) {
	    	if (AppUtil.StringIsNullOrWhiteSpace(path)) { continue; }
	    	
	    	String[] nodes = 
	    			Arrays.
	    			stream(path.replace("\\", "/").split("/")).
	    			filter(node -> !AppUtil.StringIsNullOrWhiteSpace(node)).
	    			toArray(String[]::new);
	    	
	    	elements.addAll(Arrays.asList(nodes));
	    }
	    
	    String result = String.join("/", elements);
	    
	    if (Paths.get(result).isAbsolute()) {
	        return new File(result).toURI().toString();
	    }
	    
	    return getClass().getResource("/" + result).toExternalForm();
	}	

	/**
	 * フォルダ存在確認
	 * @param targetDirectory String 対象ディレクトリ
	 * @return 存在判定
	 */
	public Boolean existsFolder(String targetDirectory) {
		
		Path path = Paths.get(targetDirectory);
		
		return (Files.exists(path) && Files.isDirectory(path));
	}
	/**
	 * フォルダ存在確認
	 * @param targetDirectory Path 対象パス
	 * @return 存在判定
	 */
	public Boolean existsFolder(Path path) {
		
		return (Files.exists(path) && Files.isDirectory(path));
	}	
	
	/**
	 * ファイル名 一覧取得
	 * @param path String resourcesフォルダからの対象パス(フォルダ：PATHの文字列)
	 * @return ファイル名の一覧
	 * @throws Exception
     * @brief resourcesフォルダ内の対象フォルダにあるファイルを名称として一覧で取得する<br>
	 */
	public List<String> getFilesNameList(String path) throws Exception {
		try{
	        // resources フォルダ内のURLを取得
	        URL url = getClass().getResource(path);
	        
	        if (url == null) throw new NoSuchFileException(path);
			
			try  (var stream = Files.list(Paths.get(url.toURI()))){
				return stream.
			    		filter(file -> !Files.isDirectory(file)).
			    		map(Path::getFileName).
			    		map(Path::toString).
			    		collect(Collectors.toList());				
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"[ファイル操作クラス] ：フォルダよりファイル一覧取得失敗", 
					e);
		}
	}

	/**
	 * ファイル(リソースURI) 一覧取得
	 * @param targetDirectory String 対象ディレクトリ(フォルダ：PATHの文字列)
	 * @return リソースURIの一覧
	 * @throws Exception
     * @brief 対象フォルダにあるファイルをリソース形式として一覧で取得する<br>
     * [file:///C: ～]形式
	 */
	public List<String> getResourceUrlFullPathList(String targetDirectory) throws Exception {
		
		try{
			Path path = new File(targetDirectory).toPath(); 
			
			try  (var stream = Files.list(path)){
				return stream.
			    		filter(file -> !Files.isDirectory(file)).
			    		map(p -> p.toUri().toString()).
			    		collect(Collectors.toList());				
			}
		} catch (Exception e) {
			throw new RuntimeException( "[ファイル操作クラス] ：フォルダよりファイル一覧取得失敗", e);
		}
	}

	/**
	 * ファイル名 一覧取得
	 * @param path String 対象ディレクトリ(フォルダ：PATHの文字列)
	 * @return ファイル名の一覧
	 * @throws Exception
     * @brief 対象フォルダにあるファイルを名称として一覧で取得する<br>
	 */
	public List<String> getFilesFullPathList(String targetDirectory) throws Exception {
		
		try{
			Path path = new File(targetDirectory).toPath(); 
			
			try  (var stream = Files.list(path)){
				return stream.
			    		filter(file -> !Files.isDirectory(file)).
			    		map(Path::getFileName).
			    		map(Path::toString).
			    		collect(Collectors.toList());				
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"[ファイル操作クラス] ：フォルダよりファイル一覧取得失敗", 
					e);
		}
	}		

}
