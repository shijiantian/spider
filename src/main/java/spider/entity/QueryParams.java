package spider.entity;

public class QueryParams {
	private int type;//1获取名单  2获取图片路径与数量等信息 3 必应参数
	private int pn;
	private int rn;
	private String area;
	private String sex;
	private String keyWord;//要检索的人员名字
	private String picSize;
	private String picColor;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getPn() {
		return pn;
	}
	public void setPn(int pn) {
		this.pn = pn;
	}
	public int getRn() {
		return rn;
	}
	public void setRn(int rn) {
		this.rn = rn;
	}
	public String getPicSize() {
		return picSize;
	}
	public void setPicSize(String picSize) {
		this.picSize = picSize;
	}
	public String getPicColor() {
		return picColor;
	}
	public void setPicColor(String picColor) {
		this.picColor = picColor;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
}
