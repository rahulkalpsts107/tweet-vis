package com.twitter.sentimental;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "docEmotions")
public class TweetEmotion {
	@XmlElement(name = "anger")
	private double anger;
	
	public double getAnger() {
		return anger;
	}

	public double getDisgust() {
		return disgust;
	}

	public double getFear() {
		return fear;
	}

	public double getJoy() {
		return joy;
	}

	public double getSadness() {
		return sadness;
	}

	@XmlElement(name = "disgust")
	private double disgust;
	
	@XmlElement(name = "fear")
	private double fear;
	
	@XmlElement(name = "joy")
	private double joy;
	
	@XmlElement(name = "sadness")
	private double sadness;

	public TweetEmotion(double anger,double disgust, double fear, double joy, double sadness) 
	{
		this.anger = anger;
		this.disgust = disgust;
		this.fear = fear;
		this.joy = joy;
		this.sadness = sadness;
	}
	
	public TweetEmotion() 
	{
		
	}

	@Override
	public String toString() {
		return "TweetEmotion [anger=" + anger + ", disgust=" + disgust + ", fear=" + fear + ", joy=" + joy
				+ ", sadness=" + sadness + "]";
	}
	
	
}
