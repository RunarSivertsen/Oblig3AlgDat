package no.oslomet.cs.algdat.Oblig3;

//Jørgen Røkke BEnder s331368
//Runar Sivertsen s331414

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

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
    endringer++;                             // en endring
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
      if (tom()) return "[]";

      Stakk<Node<T>> stakk = new TabellStakk<>();
      StringJoiner s = new StringJoiner(", ", "[", "]");

      Node<T> p = rot;
      while (p.høyre != null)
      {
          stakk.leggInn(p);
          p = p.høyre;
      }

      s.add(p.verdi.toString());

      while (true)
      {
          if (p.venstre != null)
          {
              p = p.venstre;
              while (p.høyre != null)
              {
                  stakk.leggInn(p);
                  p = p.høyre;
              }
          }
          else if (!stakk.tom()) p = stakk.taUt();
          else break;

          s.add(p.verdi.toString());
      }

      return s.toString();
      //throw new UnsupportedOperationException("Ikke kodet ennå!");
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
    if (tom()) return new String[0];

    Liste<String> liste = new TabellListe<>();

    if (!tom()){
      grener(rot, liste);
    }
    String[] a = new String[liste.antall()];
    int i = 0;
    for (String gren : liste){
      a[i++] = gren;
    }
    return a;
  }

  private static <T> void grener(Node<T> p, Liste<String> liste)
  {
    if (p.venstre == null && p.høyre == null){
      liste.leggInn(gren(p));
    }
    if (p.venstre != null){
      grener(p.venstre, liste);
    }
    if (p.høyre != null){
      grener(p.høyre, liste);
    }
  }

  public String bladnodeverdier()
  {
      if (tom()) return "[]";
      StringJoiner s = new StringJoiner(", ", "[", "]");
      bladnodeverdier(rot, s);
      return s.toString();
  }

    private static <T> void bladnodeverdier(Node<T> p, StringJoiner s)
    {
        if (p.venstre == null && p.høyre == null){
            s.add(p.verdi.toString());
        }
        if (p.venstre != null){
            bladnodeverdier(p.venstre, s);
        }
        if (p.høyre != null){
            bladnodeverdier(p.høyre, s);
        }
    }

    private static <T> Node<T> førsteNode(Node<T> p)
    {
        while (true){
            if (p.venstre != null){
                p = p.venstre;
            }
            else if (p.høyre != null){
                p = p.høyre;
            }
            else return p;
        }
    }

    public String postString()
    {
      if (tom()) return "[]";

      StringJoiner sj = new StringJoiner(", ", "[", "]");

      Node<T> p = førsteNode(rot);

      while (true){
          sj.add(p.verdi.toString());

          if (p.forelder == null){
              break;
          }

          Node<T> f = p.forelder;

          if (p == f.høyre || f.høyre == null){
              p = f;
          }
          else{
              p = førsteNode(f.høyre);
          }
      }
      return sj.toString();
  }

    private static <T> Node<T> nesteNode(Node<T> p)
    {
        Node<T> f = p.forelder;
        while (f != null && (p == f.høyre || f.høyre == null)){
            p = f; f = f.forelder;
        }
        return f == null ? null : førsteNode(f.høyre);
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
      if (tom()) return;
      p = førsteNode(rot);  // bruker en hjelpemetode
      q = null;
      removeOK = false;
      iteratorendringer = endringer;
    }


    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      if (!hasNext()) throw new NoSuchElementException("Ikke flere bladnodeverdier!");

      if (endringer != iteratorendringer) throw new
              ConcurrentModificationException("Treet har blitt endret!");

      removeOK = true;
      q = p; p = nesteNode(p);  // bruker en hjelpemetode

      return q.verdi;
      //throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
        if (!removeOK) throw
                new IllegalStateException("Ulovlig kall!");

        if (endringer != iteratorendringer) throw new
                ConcurrentModificationException("Treet er bltt endret!");

        removeOK = false;

        Node<T> f = q.forelder;

        if (f == null) rot = null;
        else if (q == f.venstre) f.venstre = null;
        else f.høyre = null;

        antall--;             // verdi er fjernet
        endringer++;          // en endring i treet
        iteratorendringer++;  // en endring gjort av iteratoren
      //throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator

} // ObligSBinTre
