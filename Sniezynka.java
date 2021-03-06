import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

// ---------------------------------------------------------------
// Glowny program uruchamiajacy okno Swinga
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
// ---------------------------------------------------------------
// Okno Swinga, ktore zawiera jeden panel
class MojeOkno extends JFrame
{
	MojeOkno() {
	
		setSize(600,600);
		MojPanel p = new MojPanel();
		add(p);
	}
}
// ---------------------------------------------------------------
// Panel, po ktorym rysowana jest Sniezynka
class MojPanel extends JPanel
{
	// Ilosc iteracji 
	static int ITER = 0;
	
	// Uchwyt do panelu, aby mozna bylo go odswiezyc w kazdej chwili
	static MojPanel Uchwyt;
	
	// Uchwyt do interfejsu graficznego
	static Graphics2D g;
	
	MojPanel() { Uchwyt = this; }
	
	// Rysowanie po panelu
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		// ustawiamy uchwyt do interfejsu graficznego
		this.g = (Graphics2D)g;
		
		RysujSniezynke(new Punkt(300,300), 3, 150);
		
		RysujSniezynke(new Punkt(500,200), 4, 50);
		
		RysujSniezynke(new Punkt(100,210), 2, 70);
		
		RysujSniezynke(new Punkt(220,100), 5, 50);
		
	}
	
	// Rysowanie sniezynki w punkcie Srodek, ktora ma LiczbaKatow i Promien
	public void RysujSniezynke(Punkt Srodek, int LiczbaKatow, int Promien)
	{
		// Musimy narysowac LiczbaKatow-kat foremny
		// Aby otrzymac punkty na okragu ulozone w wielokat foremny
		// obliczamy pierwiastek LiczbaKatow-stopnia z liczby (Promien + 0i)
		
		Punkt Pierwiastki[] = new Punkt[LiczbaKatow];
		
		for (int k=0; k<LiczbaKatow; ++k)
		{
			// kty pierwiastek
			// z_k = |Promien|^(1/LiczbaKatow) * (COS ((Phi + 2PI k)/LiczbaKatow) + i SIN((Phi + 2PI k)/LiczbaKatow) )
			Pierwiastki[k] = new Punkt();
			
			Pierwiastki[k].x = Srodek.x + (int)(Math.pow(Promien, 1) * Math.cos( (0 + 2 * Math.PI * k)/LiczbaKatow ));
			Pierwiastki[k].y = Srodek.y + (int)(Math.pow(Promien, 1) * Math.sin( (0 + 2 * Math.PI * k)/LiczbaKatow ));
			
			
			
		}
		
		//RysujKrawedzSniezynki(Pierwiastki[1], Pierwiastki[0], 1);
		
		// Rysowanie Sniezynki
		for (int k=0; k<LiczbaKatow; ++k)
		{
			RysujKrawedzSniezynki( Pierwiastki[k], Pierwiastki[(k+1)%LiczbaKatow], ITER);
		}
		
	}
	
	// Rysowanie krawedzi sniezynki iter iteracji w glab
	void RysujKrawedzSniezynki(Punkt p1, Punkt p2, int iter)
	{
		// ostatni poziom rekurencji
		// rysowanie odcinka
		if (iter == 0)
		{
			g.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
		}
		else
		{
			
			Punkt W = new Punkt();
			Punkt A = new Punkt();
			Punkt B = new Punkt();
			Punkt C = new Punkt();
			
			W.x = p2.x - p1.x;
			W.y = p2.y - p1.y;
			
			A.x = p1.x + W.x/3;
			A.y = p1.y + W.y/3;
			
			B.x = p2.x - W.x/3;
			B.y = p2.y - W.y/3;
			
		
			// Punkt C jest wynikiem obrotu punktu B wzgledem A o 60*
			// tak naprawde to -60* (odbicie wzgledem osi Y - os Y rosnie w dol na ekranie)
			C = B.Obrot(A, -Math.PI/3);

			// Rysowanie kola w punkcie A (DEBUG)
			//A.Rysuj(g);
			//B.Rysuj(g);
			//C.Rysuj(g);
			
			RysujKrawedzSniezynki(p1, A, iter-1);
			RysujKrawedzSniezynki(A, C, iter-1);
			RysujKrawedzSniezynki(C, B, iter-1);
			RysujKrawedzSniezynki(B, p2, iter-1);	
		}
	}
	
	
	

	
}
// ---------------------------------------------------------------
// Klasa-Watek ktora generuje animacje tworzenia kolejnych etapow Sniezynki
class MojWatek extends Thread
{
	public void run()
	{
		Thread t = Thread.currentThread();
		
		// Rysujemy 5 etapow rysowania Sniezynki
		for (int i=0; i<5; i++)
		{
			// Ustawiamy glebokosc rekurencji i odswiezamy
			MojPanel.ITER = i;
			MojPanel.Uchwyt.repaint();
			
			try {
				t.sleep(1000);
			}
			catch ( Exception e) { }
					
		}
		
	}
}

// ---------------------------------------------------------------
// Pomocnicza klasa do obslugi punktow plaszczyzny
class Punkt
{
	// skladowe
	public int x,y;

	// konstruktory
	Punkt() { }
	Punkt(int x, int y) { this.x = x; this.y = y; }
	
	// Tablicujemy sinusy i cosinusy do optymalizacji czasu dzialania
	static double cos60 = Math.cos(-Math.PI/3);
	static double sin60 = Math.sin(-Math.PI/3);
	
	
	// Metoda zwraca punkt obrocony o kat wzgledem punktu (0,0) 
	// Jesli pomnozymy punkt (x + iy) przez (cos(Phi) + isin(Phi)
	// otrzymamy punkt ktory jest wynikiem obrocenia punktu (x + iy) o Phi radianow
	Punkt Obrot(double kat)
	{
		Punkt ret = new Punkt();
		ret.x = (int)(x * Math.cos(kat) - y * Math.sin(kat));
		ret.y = (int)(x * Math.sin(kat) + y * Math.cos(kat));
		return ret;
	}
	
	// Metoda zwraca punkt obrocony o kat wzgledem punktu Wzgledem
	// Podobnie jak wyzej, dodatkowo przesuwamy uklad wspolrzednych
	// do punktu (x,y), obracamy, a na koniec wracamy do pierwotnego
	// ukladu wspolrzednych
	Punkt Obrot(Punkt Wzgledem, double kat)
	{
		Punkt ret = new Punkt();
		ret.x = (int)((x-Wzgledem.x) * Math.cos(kat) - (y-Wzgledem.y) * Math.sin(kat));
		ret.y = (int)((x-Wzgledem.x) * Math.sin(kat) + (y-Wzgledem.y) * Math.cos(kat));
		
		ret.x += Wzgledem.x;
		ret.y += Wzgledem.y;
		
		return ret;
		
	}
	
	// Zoptymalizowana metoda, ktora zwraca punkt obrocony o 60 stopni
	Punkt Obrot60()
	{
		Punkt ret = new Punkt
		(
			(int)(x * cos60 - y * sin60), 
			(int)(x * sin60 + y * cos60)
		);
		return ret;
	}
	
	// Metoda rysujaca okrag w punkcie
	void Rysuj(Graphics2D g)
	{
		g.draw(new Arc2D.Double(x-5, y-5, 10, 10, 0, 360, 0));	
	}
}

