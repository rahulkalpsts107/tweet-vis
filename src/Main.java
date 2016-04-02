import com.twitter.sentimental.Constants;
import com.twitter.sentimental.SentimentAnalyzer;
/*Only to test sentiment analyze from watson*/
public class Main 
{
    
	public static void main(String []args)
	{
		try 
		{
			SentimentAnalyzer analyzer = new SentimentAnalyzer();
			String analyzeResult = analyzer.analyzeText("I hate pineapples but love apples");
			System.out.println(analyzeResult);
			
			analyzeResult = analyzer.getTargettedSentiment(Constants.slang1);
			System.out.println(analyzeResult);
			
			analyzeResult = analyzer.getEmotions(Constants.slang1);
			System.out.println(analyzeResult);

		}
		catch(Exception exp)
		{
			System.out.println("Error"+exp.getMessage());
		}
	}	
}