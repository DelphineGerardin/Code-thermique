import java.util.ArrayList;


public class Reactor 
{
	double power = 3E9;
	double pExtract;
	double fuelTemperature = 1000;
	double fuelEnergy;
	double intermediateTemperature = 820;
	Geometry geometry;
	Zone heatExchanger;
	Zone core;
	Zone superiorHorizontalPipe;
	Zone superiorVerticalPipe;
	Zone inferiorHorizontalPipe;
	Zone inferiorVerticalPipe;
	
	ArrayList<Cell> cellList;
	
	public Reactor(ArrayList<Cell> cellList)
	{
		this.cellList = cellList;
		
		heatExchanger = new Zone(2);
		core = new Zone(2);
		superiorHorizontalPipe = new Zone(0);
		superiorVerticalPipe = new Zone(2);
		inferiorHorizontalPipe = new Zone(0);
		inferiorVerticalPipe = new Zone(2);
		
		this.geometry = new Geometry(heatExchanger, 
						inferiorVerticalPipe, 
						inferiorHorizontalPipe, 
						core, 
						superiorHorizontalPipe, 
						superiorVerticalPipe);
	}
	
	public void computeGlobalParameters()
	{
		this.fuelTemperature = 0;
		this.fuelEnergy = 0;
		double energyPerKelvin = 0;
		
		this.power = 0;
		this.pExtract =0;
		
		for (int i = 0; i<cellList.size(); i++)
		{
			// for temperature computation
			energyPerKelvin += cellList.get(i).rho*cellList.get(i).cp*cellList.get(i).volume;
			fuelEnergy += cellList.get(i).rho*cellList.get(i).cp*cellList.get(i).volume* cellList.get(i).temperature;
		
			// for power computation
			this.power += cellList.get(i).pVol*cellList.get(i).volume;
			this.pExtract += cellList.get(i).pExtract*cellList.get(i).volume;
			
		}
		this.fuelTemperature = fuelEnergy/energyPerKelvin;
	}
	
	public void setPowerCos(double power)
	{
		double Ptot = 0;
		double wtot =0;
		
		double h = geometry.coreHeight;
		double z = 0;
		double dz = 0;
		double weightingFactor = 0;
		double volume = 0;
		for (int i = 0; i<core.cellList.size(); i++)
		{
			volume = core.cellList.get(i).volume;
			dz = 2*core.cellList.get(i).dx[core.cellList.get(i).direction];
			weightingFactor = (Math.sin((Math.PI/h)*(z+dz-h/2))-Math.sin((Math.PI/h)*(z-h/2)))/2;
			core.cellList.get(i).pVol = power * weightingFactor / volume;
			
			z += dz;
			
		}
		
	}
	public void setPowerHomo(double power)
	{
		for (int i = 0; i<cellList.size(); i++)
		{
			cellList.get(i).pVol = power / this.geometry.volume;
		}
	}

}
