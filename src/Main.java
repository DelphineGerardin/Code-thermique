import java.util.ArrayList;


public class Main {
	
	public static void main(String[] args) 
	{

	Scribe sTemperature = new Scribe("temperature");
	Scribe sPower = new Scribe("power");
	Scribe sPExtract = new Scribe("pExtract");
	Scribe shS = new Scribe("hS");
	Scribe sFuelEnergy = new Scribe("fuelEnergy");
	
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	
	double powerVariation[] = {3E9/16, 2E9/16, 50, 60};
	double power = powerVariation[0];
	
	Reactor msfr = new Reactor(cellList);	
	msfr.geometry.createCellList(cellList);
	double timeStep = 0.001;
	
	
	// Mise à l'équilibre du système
	
	msfr.setPowerHomo(power);
	//msfr.setPowerCos(power);
	msfr.computeGlobalParameters();
	cellList.get(0).setHS(msfr.power/(cellList.get(0).temperature-msfr.intermediateTemperature));
	
	
	double time = 0;
	double savingTime = 0;
	double hS_old = 0;
	double hS = 0;
	
	do{
	//for (double time = 0; time<10; time +=timeStep)
	//{
		hS_old = hS;
		cellList.get(0).setHS(msfr.power/(cellList.get(0).temperature-msfr.intermediateTemperature));
		cellList.get(0).computePExtract();
		
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_dTdt();
		}
		
		for (int i = 0; i< cellList.size(); i++)
		{
			
			cellList.get(i).compute_T(time, timeStep);
		}
		
		msfr.computeGlobalParameters();
		
		/*sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
		sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
		sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
		shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
		sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));*/
		
		hS = cellList.get(0).hS;
		time += timeStep;
	//}
	}while (Math.abs((hS-hS_old)/hS_old) > 1E-10 || time < 10*timeStep);

	// Transitoire
	for (time = 0; time<100; time +=timeStep)
	{
		if (time > powerVariation[2] && time <= powerVariation[3])
		{
			power += (powerVariation[1] - powerVariation[0])*timeStep/(powerVariation[3]-powerVariation[2]);
			//msfr.setPowerCos(power);
			msfr.setPowerHomo(power);
		}
			
		//cellList.get(0).setPExtract(power/cellList.get(0).volume);
		cellList.get(0).computePExtract();
		
		
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_dTdt();
			//System.out.println(cellList.get(i).pExtract);
		}
		
		for (int i = 0; i< cellList.size(); i++)
		{
			cellList.get(i).compute_T(time, timeStep);
			if (time > savingTime)
			{
				cellList.get(i).sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(i).temperature));
			}
			
		}
			
		msfr.computeGlobalParameters();
		
		if (time > savingTime)
		{
			sTemperature.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelTemperature));
			sPower.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.power));
			sPExtract.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.pExtract));
			shS.addValue(String.valueOf(time)+"	"+String.valueOf(cellList.get(0).hS));
			sFuelEnergy.addValue(String.valueOf(time)+"	"+String.valueOf(msfr.fuelEnergy));
			
			savingTime = time + 0.1;
		}
		
	}
	
	sTemperature.writeInFile();
	sPower.writeInFile();
	sPExtract.writeInFile();
	shS.writeInFile();
	sFuelEnergy.writeInFile();
	
	for (int i=0; i<cellList.size(); i++)
	{
		cellList.get(i).sTemperature.writeInFile();
	}
	
	}
}
