/**
 * 
 */
package com.decisiontree.common;

import java.util.HashMap;
import java.util.Map;

import com.opencsv.bean.CsvBind;

 /**
  * This class represents input Feature vector.
  * 
  * @author Kanchan Waikar
  * Date Created : 3:16:19 PM
  *
  */
public class FeatureVector {
	@CsvBind
	int XB;
	@CsvBind
	int XC;
	@CsvBind
	int XD;
	@CsvBind
	int XE;
	@CsvBind
	int XF;
	@CsvBind
	int XG;
	@CsvBind
	int XH;
	@CsvBind
	int XI;
	@CsvBind
	int XJ;
	@CsvBind
	int XK;
	@CsvBind
	int XL;
	@CsvBind
	int XM;
	@CsvBind
	int XN;
	@CsvBind
	int XO;
	@CsvBind
	int XP;
	@CsvBind
	int XQ;
	@CsvBind
	int XR;
	@CsvBind
	int XS;
	@CsvBind
	int XT;
	@CsvBind
	int XU;
	@CsvBind
	int Class;
	Map<String,Integer> inputVectorMap = new HashMap<String,Integer>();
	
	/**
	 * This function returns user input in Map.
	 * @return
	 */
	public Map<String,Integer> getInputMap()
	{
		if(inputVectorMap.size()==0)
		{
			inputVectorMap.put("XB", XB);
			inputVectorMap.put("XC", XC);
			inputVectorMap.put("XD", XD);
			inputVectorMap.put("XE", XE);
			inputVectorMap.put("XF", XF);
			inputVectorMap.put("XG", XG);
			inputVectorMap.put("XH", XH);
			inputVectorMap.put("XI", XI);
			inputVectorMap.put("XJ", XJ);
			inputVectorMap.put("XK", XK);
			inputVectorMap.put("XL", XL);
			inputVectorMap.put("XM", XM);
			inputVectorMap.put("XN", XN);
			inputVectorMap.put("XO", XO);
			inputVectorMap.put("XP", XP);
			inputVectorMap.put("XQ", XQ);
			inputVectorMap.put("XR", XR);
			inputVectorMap.put("XS", XS);
			inputVectorMap.put("XT", XT);
			inputVectorMap.put("XU", XU);
			return inputVectorMap;
		}
		else
		{
			return inputVectorMap;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FeatureVector [XB=" + XB + ", XC=" + XC + ", XD=" + XD + ", XE=" + XE + ", XF=" + XF + ", XG=" + XG
				+ ", XH=" + XH + ", XI=" + XI + ", XJ=" + XJ + ", XK=" + XK + ", XL=" + XL + ", XM=" + XM + ", XN=" + XN
				+ ", XO=" + XO + ", XP=" + XP + ", XQ=" + XQ + ", XR=" + XR + ", XS=" + XS + ", XT=" + XT + ", XU=" + XU
				+ ", Class=" + Class + "]";
	}

	/**
	 * @return the xB
	 */
	public int isXB() {
		return XB;
	}

	/**
	 * @param xB
	 *            the xB to set
	 */
	public void setXB(int xB) {
		XB = xB;
	}

	/**
	 * @return the xC
	 */
	public int isXC() {
		return XC;
	}

	/**
	 * @param xC
	 *            the xC to set
	 */
	public void setXC(int xC) {
		XC = xC;
	}

	/**
	 * @return the xD
	 */
	public int isXD() {
		return XD;
	}

	/**
	 * @param xD
	 *            the xD to set
	 */
	public void setXD(int xD) {
		XD = xD;
	}

	/**
	 * @return the xE
	 */
	public int isXE() {
		return XE;
	}

	/**
	 * @param xE
	 *            the xE to set
	 */
	public void setXE(int xE) {
		XE = xE;
	}

	/**
	 * @return the xF
	 */
	public int isXF() {
		return XF;
	}

	/**
	 * @param xF
	 *            the xF to set
	 */
	public void setXF(int xF) {
		XF = xF;
	}

	/**
	 * @return the xG
	 */
	public int isXG() {
		return XG;
	}

	/**
	 * @param xG
	 *            the xG to set
	 */
	public void setXG(int xG) {
		XG = xG;
	}

	/**
	 * @return the xH
	 */
	public int isXH() {
		return XH;
	}

	/**
	 * @param xH
	 *            the xH to set
	 */
	public void setXH(int xH) {
		XH = xH;
	}

	/**
	 * @return the xI
	 */
	public int isXI() {
		return XI;
	}

	/**
	 * @param xI
	 *            the xI to set
	 */
	public void setXI(int xI) {
		XI = xI;
	}

	/**
	 * @return the xJ
	 */
	public int isXJ() {
		return XJ;
	}

	/**
	 * @param xJ
	 *            the xJ to set
	 */
	public void setXJ(int xJ) {
		XJ = xJ;
	}

	/**
	 * @return the xK
	 */
	public int isXK() {
		return XK;
	}

	/**
	 * @param xK
	 *            the xK to set
	 */
	public void setXK(int xK) {
		XK = xK;
	}

	/**
	 * @return the xL
	 */
	public int isXL() {
		return XL;
	}

	/**
	 * @param xL
	 *            the xL to set
	 */
	public void setXL(int xL) {
		XL = xL;
	}

	/**
	 * @return the xM
	 */
	public int isXM() {
		return XM;
	}

	/**
	 * @param xM
	 *            the xM to set
	 */
	public void setXM(int xM) {
		XM = xM;
	}

	/**
	 * @return the xN
	 */
	public int isXN() {
		return XN;
	}

	/**
	 * @param xN
	 *            the xN to set
	 */
	public void setXN(int xN) {
		XN = xN;
	}

	/**
	 * @return the xO
	 */
	public int isXO() {
		return XO;
	}

	/**
	 * @param xO
	 *            the xO to set
	 */
	public void setXO(int xO) {
		XO = xO;
	}

	/**
	 * @return the xP
	 */
	public int isXP() {
		return XP;
	}

	/**
	 * @param xP
	 *            the xP to set
	 */
	public void setXP(int xP) {
		XP = xP;
	}

	/**
	 * @return the xQ
	 */
	public int isXQ() {
		return XQ;
	}

	/**
	 * @param xQ
	 *            the xQ to set
	 */
	public void setXQ(int xQ) {
		XQ = xQ;
	}

	/**
	 * @return the xR
	 */
	public int isXR() {
		return XR;
	}

	/**
	 * @param xR
	 *            the xR to set
	 */
	public void setXR(int xR) {
		XR = xR;
	}

	/**
	 * @return the xS
	 */
	public int isXS() {
		return XS;
	}

	/**
	 * @param xS
	 *            the xS to set
	 */
	public void setXS(int xS) {
		XS = xS;
	}

	/**
	 * @return the xT
	 */
	public int isXT() {
		return XT;
	}

	/**
	 * @param xT
	 *            the xT to set
	 */
	public void setXT(int xT) {
		XT = xT;
	}

	/**
	 * @return the xU
	 */
	public int isXU() {
		return XU;
	}

	/**
	 * @param xU
	 *            the xU to set
	 */
	public void setXU(int xU) {
		XU = xU;
	}

	/**
	 * @return the class
	 */
	public int isClass() {
		return Class;
	}

	/**
	 * @param class1
	 *            the class to set
	 */
	public void setClass(int class1) {
		Class = class1;
	}

}
