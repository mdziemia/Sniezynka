import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

class Sniezynka
{
	public static void main(String[] args)
	{
		MojeOkno o = new MojeOkno();
		o.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		o.setVisible(true);
		
		MojWatek w = new MojWatek();
		w.start();
	}
}

class MojeOkno extends JFrame
{
	MojeOkno() {
	
		setSize(600,600);
		MojPanel p = new MojPanel();
		add(p);
	}
}

class Punkt
{
	public int x,y;
	Punkt() { }
	Punkt(int x, int y) { this.x = x; this.y = y; }
	
	static double coss = Math.cos(-Math.PI/3);
	static double sins = Math.sin(-Math.PI/3);
	
	// Metoda zwraca punkt obrocony o kat wzgledem punktu (0,0) 
	Punkt Obrot(double kat)
	{
		Punkt ret = new Punkt();
		ret.x = (int)(x * Math.cos(kat) - y * Math.sin(kat));
		ret.y = (int)(x * Math.sin(kat) + y * Math.cos(kat));
		return ret;
	}
	
	Punkt Obrot60()
	{
		Punkt ret = new Punkt();
		ret.x = (int)(x * coss - y * sins);
		ret.y = (int)(x * sins + y * coss);
		return ret;
	}
	
	void Rysuj(Graphics2D g)
	{
		g.draw(new Arc2D.Double(x-5, y-5, 10, 10, 0, 360, 0));	
	}
}

class MojPanel extends JPanel
{
	static int ITER = 0;
	static MojPanel p;
	
	MojPanel() { p = this; }
	
	void RysujSniezke(Punkt p1, Punkt p2, int iter, Graphics2D g)
	{
		if (iter == 0)
		{
			g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
		}
		else
		{
			
			Punkt w = new Punkt(p2.x - p1.x, p2.y - p1.y);
			Punkt A = new Punkt();
			Punkt B = new Punkt();
			
			A.x = p1.x + w.x/3;
			A.y = p1.y + w.y/3;
			
			B.x = p2.x - w.x/3;
			B.y = p2.y - w.y/3;
			
			// Rysowanie kola w punkcie A (sprawdzenie)
			//A.Rysuj(g);
			//B.Rysuj(g);
			
		
			// Punkt B w ukladzie w ktorym A jest poczatkiem ukladu
			Punkt Bprim = new Punkt(B.x - A.x, B.y - A.y);
			Punkt Bobrot = Bprim.Obrot60();
			// Punkt B przesuwamy z powrotem na swoje miejsce
			Punkt C = new Punkt(Bobrot.x + A.x, Bobrot.y + A.y);
			//C.Rysuj(g);
			
			RysujSniezke(p1, A, iter-1, g);
			RysujSniezke(A, C, iter-1, g);
			RysujSniezke(C, B, iter-1, g);
			RysujSniezke(B, p2, iter-1, g);
			
		}
		
		
		
		
	
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		
		
		RysujSniezke(new Punkt(100,400), new Punkt(250,100), ITER, g2);
		RysujSniezke(new Punkt(250,100), new Punkt(400,400), ITER, g2);
		RysujSniezke(new Punkt(400,400), new Punkt(100,400), ITER, g2);
		
		
	}
}

class MojWatek extends Thread
{
	public void run()
	{
		Thread t = Thread.currentThread();
		
		for (int i=0; i<6; i++)
		{
			MojPanel.ITER = i;
			MojPanel.p.repaint();
			
			try {
				t.sleep(1000);
			}
			catch ( Exception e) { }
					
		}
		
	}
}
