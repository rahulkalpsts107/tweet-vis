package com.twitter.sentimental;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "results")
public class EmotionResults 
{
	@Override
	public String toString() {
		return "EmotionResults [status=" + status + ", totalTransactions=" + totalTransactions
				+ ", language=" + language + ", docEmotions=" + docEmotions + "]";
	}
	@XmlElement(name = "status")
	private String status;
	
	@XmlElement(name = "usage")
	private String usage;
	
	@XmlElement(name = "totalTransactions")
	private String totalTransactions;
	
	@XmlElement(name = "language")
	private String language;
	
	@XmlElement(name = "docEmotions")
	private TweetEmotion docEmotions;
	
	public String getStatus() {
		return status;
	}
	public String getUsage() {
		return usage;
	}
	public String getTotalTransactions() {
		return totalTransactions;
	}
	public String getLanguage() {
		return language;
	}
	public TweetEmotion getDocEmotions() {
		return docEmotions;
	}
}
