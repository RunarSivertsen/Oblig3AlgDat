package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v; høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }
  
  @Override
  public boolean leggInn(T verdi)
  {
    Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

    Node<T> p = rot, q = null;               // p starter i roten
    int cmp = 0;                             // hjelpevariabel

    while (p != null)       // fortsetter til p er ute av treet
    {
      q = p;                                 // q er forelder til p
      cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
      p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
    }

    // p er nå null, dvs. ute av treet, q er den siste vi passerte

    p = new Node<>(verdi, q);                   // oppretter en ny node

    if (q == null) rot = p;                  // p blir rotnode
    else if (cmp < 0) q.venstre = p;         // venstre barn til q
    else q.høyre = p;                        // høyre barn til q

    antall++;                                // én verdi mer i treet
    return true;                             // vellykket innlegging
  }
  
  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }



  @Override
  public boolean fjern(T verdi)
  {
    if (verdi == null) return false;  // treet har ingen nullverdier

    Node<T> p = rot, q = null;        // q skal være forelder til p

    while (p != null)                 // leter etter verdi
    {
      int cmp = comp.compare(verdi,p.verdi);      // sammenligner
      if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
      else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
      else break;    // den søkte verdien ligger i p
    }
    if (p == null) return false;   // finner ikke verdi

    if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
    {
      Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn

      if (b != null) b.forelder = q;  //Ny kode, sørger for at forelder får korrekt verdi

      if (p == rot) rot = b;
      else if (p == q.venstre) q.venstre = b;
      else q.høyre = b;
    }
    else  // Tilfelle 3)
    {
      Node<T> s = p, r = p.høyre;   // finner neste i inorden
      while (r.venstre != null)
      {
        s = r;    // s er forelder til r
        r = r.venstre;
      }

      p.verdi = r.verdi;   // kopierer verdien i r til p

      if (r.høyre != null) r.høyre.forelder = s; //Ny kode, sørger for at forelder får korrekt verdi

      if (s != p) s.venstre = r.høyre;
      else s.høyre = r.høyre;
    }

    antall--;     // det er nå én node mindre i treet
    endringer++;  // en endring

    return true;
  }
  
  public int fjernAlle(T verdi)
  {
    int antallFjernet = 0;
    while(fjern(verdi)){
      antallFjernet++;
    }
    return antallFjernet;
  }
  
  @Override
  public int antall()
  {
    return antall;
  }
  
  public int antall(T verdi)
  {
    if(verdi == null) return 0;

    Node<T> p = rot;
    int antallAvVerdi = 0;

    while(p != null){
        int cmp = comp.compare(verdi, p.verdi);
        if(cmp < 0){
            p = p.venstre;
        }
        else{
            if (cmp == 0){
                antallAvVerdi++;
            }
            p = p.høyre;
        }
    }
    return antallAvVerdi;
  }
  
  @Override
  public boolean tom()
  {
    return antall == 0;
  }
  
  @Override
  public void nullstill()
  {
    if (!tom()){
      nullstill(rot);
    }
    rot = null;
    antall = 0;
    endringer++;
  }

  private static <T> void nullstill(Node<T> p)
  {
    if (p.venstre != null){
      nullstill(p.venstre);
      p.venstre = null;
    }
    if (p.høyre != null){
      nullstill(p.høyre);
      p.høyre = null;
    }
    p.verdi = null;
  }


  //Oppg 3
  private static <T> Node<T> nesteInorden(Node<T> p) {
    if (p.høyre != null)
    {
      p = p.høyre;
      while (p.venstre != null) p = p.venstre;
    }
    else
    {
      while (p.forelder != null && p == p.forelder.høyre)
      {
        p = p.forelder;
      }

      p = p.forelder;
    }

    return p;
    //throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public String toString() {
    if (tom()) return "[]";

    StringJoiner s = new StringJoiner(", ", "[", "]");

    Node<T> p = rot;  // går til den første i inorden
    while (p.venstre != null) p = p.venstre;

    while (p != null)
    {
      s.add(p.verdi.toString());
      p = nesteInorden(p);
    }

    return s.toString();
    //throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  // slutt Oppg 3
  
  public String omvendtString()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String høyreGren()
  {
    StringJoiner s = new StringJoiner(", ", "[", "]");

    if (!tom()) {
      Node<T> p = rot;
      while (true) {
        s.add(p.verdi.toString());
        if (p.høyre != null){
          p = p.høyre;
        }
        else if (p.venstre != null){
          p = p.venstre;
        }
        else break;
      }
    }
    return s.toString();
  }
  
  public String lengstGren()
  {
    if (tom()) return "[]";

    Kø<Node<T>> kø = new TabellKø<>();
    kø.leggInn(rot);

    Node<T> p = null;

    while (!kø.tom()) {
      p = kø.taUt();
      if (p.høyre != null){
        kø.leggInn(p.høyre);
      }
      if (p.venstre != null){
        kø.leggInn(p.venstre);
      }
    }
    return gren(p);
  }

  private static <T> String gren(Node<T> p)
  {
    Stakk<T> s = new TabellStakk<>();
    while (p != null) {
      s.leggInn(p.verdi);
      p = p.forelder;
    }
    return s.toString();
  }
  
  public String[] grener()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  public String bladnodeverdier()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String postString()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    
    private BladnodeIterator()  // konstruktør
    {

    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator

  public static void main(String[] args) {
    Integer[] a = {4,7,2,9,5,10,8,1,3,6,4,4,4,4};
    ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
    for (int verdi : a){
      tre.leggInn(verdi);
    }
    System.out.println(tre.antall());
      System.out.println(tre.antall(5));
      System.out.println(tre.antall(4));
      System.out.println(tre.antall(-55));
  }

} // ObligSBinTre
