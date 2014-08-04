package model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer; 
import org.apache.lucene.analysis.TokenStream; 
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
 
//测试"庖丁解牛"中文分词器的分词效果     
public class Participle {
	public static ArrayList<String> displayTokenStream(TokenStream ts) throws IOException
	{
		ArrayList<String> list = new ArrayList<>();
		try {
			TermAttribute termAtt = (TermAttribute)ts.addAttribute(TermAttribute.class);
			//TypeAttribute typeAtt = (TypeAttribute)ts.addAttribute(TypeAttribute.class);
			
			while (ts.incrementToken())
			{
				String term = termAtt.term();
				
				if(stopWord(term)){
					list.add(term);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	private static Boolean stopWord(String term){
		String[] delcontword = { "年", "月", "日", "但", "如", "并", "且", "这", "很", "即"
				,"还", "而", "就", "怎", "今", "你", "我", "他", "她", "的", "是", "经"
				,"后", "已", "一", "人", "所", "由", "于", "多", "少", "不", "为"
				,"在", "来", "非", "前", "以", "该", "得", "谢", "之", "下", "或" ,"什","么"
				,"家","号","楼","#","室",";",":","请","勿","侧","单元"
				};
		String[] delword ={"远","近","大","小","东","西","南","北"};
		try {
			for (String del : delcontword) {
				if (term.contains(del)) {
					return false;
				}
			}
			for (String del : delword) {
				if (term.equals(del)) {
					return false;
				}
			}
			Pattern p = Pattern.compile(".*\\w+.*");
			Matcher m = p.matcher(term);
			if (m.matches()){
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
    public static void main(String[] args) throws Exception {
        Analyzer analyzer = new PaodingAnalyzer(); 
        String  indexStr = "我的QQ号码是24号楼，他的QQ号码是23号楼，我累个擦的擦"; 
        StringReader reader = new StringReader(indexStr); 
        TokenStream ts = analyzer.tokenStream(indexStr, reader); 
        ArrayList<String> list = Participle.displayTokenStream(ts);
    } 
} 