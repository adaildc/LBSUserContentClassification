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
 
//����"�Ҷ���ţ"���ķִ����ķִ�Ч��     
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
		String[] delcontword = { "��", "��", "��", "��", "��", "��", "��", "��", "��", "��"
				,"��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��"
				,"��", "��", "һ", "��", "��", "��", "��", "��", "��", "��", "Ϊ"
				,"��", "��", "��", "ǰ", "��", "��", "��", "л", "֮", "��", "��" ,"ʲ","ô"
				,"��","��","¥","#","��",";",":","��","��","��","��Ԫ"
				};
		String[] delword ={"Զ","��","��","С","��","��","��","��"};
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
        String  indexStr = "�ҵ�QQ������24��¥������QQ������23��¥�����۸����Ĳ�"; 
        StringReader reader = new StringReader(indexStr); 
        TokenStream ts = analyzer.tokenStream(indexStr, reader); 
        ArrayList<String> list = Participle.displayTokenStream(ts);
    } 
} 