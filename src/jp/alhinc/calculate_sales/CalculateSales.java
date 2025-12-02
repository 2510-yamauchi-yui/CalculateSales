package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//指定したパスに存在するすべてのファイルの情報を格納
		File[] files = new File(args[0]).listFiles();

		//先にファイル情報格納するList宣言
		List<File> rcdFiles = new ArrayList<>();

		//Filesの数だけ繰り返すことで、指定したパスに存在するすべてのファイルの数繰り返し
		for(int i = 0; i < files.length; i++) {

			//ファイル名判定
			if(files[i].getName().matches("^[0-9]{8}\\.rcd$")) {

				//条件に当てはまったものだけList(ArrayList)に追加する
				rcdFiles.add(files[i]);
			}
		}

		//rcdFilesに格納している数繰り返す
		for(int i = 0; i < rcdFiles.size(); i++) {
			//売上ファイルの1行目支店コード、2行目売上金額
			BufferedReader br = null;

			try {
				File file = rcdFiles.get(i);
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				//売上ファイルの中身を保持するList宣言
				List<String> salesRecord = new ArrayList<String>();

				String line;
				//ファイルの中身読み込み、0に支店コード、1に売上
				while((line = br.readLine()) != null) {

					salesRecord.add(line);
				}
				//売上ファイルから読み込んだ売上金額を加算するために型の変換を行う
				long fileSale = Long.parseLong(salesRecord.get(1));

				//支店コード
				String branch = salesRecord.get(0);

				//読み込んだ売上金額を加算する
				Long saleAmount = branchSales.get(branch) + fileSale;

				//マップの要素追加
				branchSales.put(branch, saleAmount);

			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			} finally {
				// ファイルを開いている場合
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
			}
		}

		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス C:\\Users\\trainee1308\\Desktop\\売り上げ集計課題
	 * @param ファイル名 brench.lst
	 * @param 支店コードと支店名を保持するMap branchNames
	 * @param 支店コードと売上金額を保持するMap branchSales
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path,fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				//,で分割
				String[] items = line.split(",");

				//Mapに追加する情報をputの引数として設定する
				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);
			}
		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;

		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			//keyの取得
			for(String key : branchNames.keySet()) {
				//それぞれのマップから要素を取得
				//支店名
				String name = branchNames.get(key);
				//売上
				Long sale = branchSales.get(key);
				//書き込み（支店コード,支店名,売上）
				bw.write(key + "," + name + "," + sale);
				//改行
				bw.newLine();
			}
			return true;

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
	}
}
