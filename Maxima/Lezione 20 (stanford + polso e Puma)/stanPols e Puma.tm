<TeXmacs|1.99.11>

<style|<tuple|generic|italian|maxima>>

<\body>
  <\with|prog-scripts|maxima>
    \;
  </with>

  <doc-data|<doc-title|Cinematica Inversa per Disaccoppiamento>>

  In questa relazione andremo a vedere come si calcolano le cinematiche
  inverse totali di 2 robot:

  <\itemize>
    <item>Stanford + Polso Sferico

    <item>Antropomorfo + Polso Sferico
  </itemize>

  Per prima cosa è necessario trovare i 2 termini costanti che permettono la
  disaccoppiazione della struttura con il polso

  <section|Calcolo costanti Polso Sferico>

  <\session|maxima|default>
    <\input>
      <with|color|red|(<with|math-font-family|rm|%i>6) >
    <|input>
      kill(all)$

      load("../libraryProcedure.mac")$
    </input>

    <\unfolded-io>
      <with|color|red|(<with|math-font-family|rm|%i>2) >
    <|unfolded-io>
      polsCin:matrix(

      \ \ \ \ \ \ \ \ [-(s(q[4])*s(q_6)-c(q_4)*c(q_5)*c(q_6)),
      -(c(q_4)*c(q_5)*s(q_6)+s(q_4)*c(q_6)), \ c(q_4)*s(q_5),
      \ L_6*c(q_4)*s(q_5)],

      \ \ \ \ \ \ \ \ [c(q_4)*s(q_6)+s(q_4)*c(q_5)*c(q_6),
      \ \ \ -(s(q_4)*c(q_5)*s(q_6)-c(q_4)*c(q_6)), \ s(q_4)*s(q_5),
      \ L_6*s(q_4)*s(q_5)],

      \ \ \ \ \ \ \ \ [-s(q_5)*c(q_6), \ \ \ s(q_5)*s(q_6), \ c(q_5),
      L_6*c(q_5)],

      \ \ \ \ \ \ \ \ [0, 0, \ 0, \ 1]

      \ \ \ \ );
    <|unfolded-io>
      <math|<with|math-display|true|<text|<with|font-family|tt|color|red|(<with|math-font-family|rm|%o2>)
      >><matrix|<tformat|<table|<row|<cell|c<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_5>|)>*c<around*|(|<with|math-font-family|rm|q_6>|)>-s<around*|(|q<rsub|4>|)>*s<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|-c<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_5>|)>*s<around*|(|<with|math-font-family|rm|q_6>|)>-s<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|c<around*|(|<with|math-font-family|rm|q_4>|)>*s<around*|(|<with|math-font-family|rm|q_5>|)>>|<cell|<with|math-font-family|rm|L_6>*c<around*|(|<with|math-font-family|rm|q_4>|)>*s<around*|(|<with|math-font-family|rm|q_5>|)>>>|<row|<cell|c<around*|(|<with|math-font-family|rm|q_4>|)>*s<around*|(|<with|math-font-family|rm|q_6>|)>+s<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_5>|)>*c<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|c<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_6>|)>-s<around*|(|<with|math-font-family|rm|q_4>|)>*c<around*|(|<with|math-font-family|rm|q_5>|)>*s<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|s<around*|(|<with|math-font-family|rm|q_4>|)>*s<around*|(|<with|math-font-family|rm|q_5>|)>>|<cell|<with|math-font-family|rm|L_6>*s<around*|(|<with|math-font-family|rm|q_4>|)>*s<around*|(|<with|math-font-family|rm|q_5>|)>>>|<row|<cell|-s<around*|(|<with|math-font-family|rm|q_5>|)>*c<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|s<around*|(|<with|math-font-family|rm|q_5>|)>*s<around*|(|<with|math-font-family|rm|q_6>|)>>|<cell|c<around*|(|<with|math-font-family|rm|q_5>|)>>|<cell|<with|math-font-family|rm|L_6>*c<around*|(|<with|math-font-family|rm|q_5>|)>>>|<row|<cell|0>|<cell|0>|<cell|0>|<cell|1>>>>>>>
    </unfolded-io>

    <\unfolded-io>
      <with|color|red|(<with|math-font-family|rm|%i>3) >
    <|unfolded-io>
      /* Definisco su quale robot lavoro*/

      end:polsCin$

      \;

      d36:submatrix(4,end,1,2,3);

      R36:submatrix(4,end,4);
    <|unfolded-io>
      <math|<with|math-display|true|<text|<with|font-family|tt|color|red|(<with|math-font-family|rm|%o4>)
      >><matrix|<tformat|<table|<row|<cell|<with|math-font-family|rm|L_6>*cos
      <with|math-font-family|rm|q_4>*sin <with|math-font-family|rm|q_5>>>|<row|<cell|<with|math-font-family|rm|L_6>*sin
      <with|math-font-family|rm|q_4>*sin <with|math-font-family|rm|q_5>>>|<row|<cell|<with|math-font-family|rm|L_6>*cos
      <with|math-font-family|rm|q_5>>>>>>>>

      <math|<with|math-display|true|<text|<with|font-family|tt|color|red|(<with|math-font-family|rm|%o5>)
      >><matrix|<tformat|<table|<row|<cell|cos
      <with|math-font-family|rm|q_4>*cos <with|math-font-family|rm|q_5>*cos
      <with|math-font-family|rm|q_6>-sin <with|math-font-family|rm|q_4>*sin
      <with|math-font-family|rm|q_6>>|<cell|-cos
      <with|math-font-family|rm|q_4>*cos <with|math-font-family|rm|q_5>*sin
      <with|math-font-family|rm|q_6>-sin <with|math-font-family|rm|q_4>*cos
      <with|math-font-family|rm|q_6>>|<cell|cos
      <with|math-font-family|rm|q_4>*sin <with|math-font-family|rm|q_5>>>|<row|<cell|cos
      <with|math-font-family|rm|q_4>*sin <with|math-font-family|rm|q_6>+sin
      <with|math-font-family|rm|q_4>*cos <with|math-font-family|rm|q_5>*cos
      <with|math-font-family|rm|q_6>>|<cell|cos
      <with|math-font-family|rm|q_4>*cos <with|math-font-family|rm|q_6>-sin
      <with|math-font-family|rm|q_4>*cos <with|math-font-family|rm|q_5>*sin
      <with|math-font-family|rm|q_6>>|<cell|sin
      <with|math-font-family|rm|q_4>*sin <with|math-font-family|rm|q_5>>>|<row|<cell|-sin
      <with|math-font-family|rm|q_5>*cos <with|math-font-family|rm|q_6>>|<cell|sin
      <with|math-font-family|rm|q_5>*sin <with|math-font-family|rm|q_6>>|<cell|cos
      <with|math-font-family|rm|q_5>>>>>>>>
    </unfolded-io>

    <\input>
      <with|color|red|(<with|math-font-family|rm|%i>6) >
    <|input>
      \;
    </input>
  </session>
</body>

<initial|<\collection>
</collection>>

<\references>
  <\collection>
    <associate|auto-1|<tuple|1|?|../../../../../.TeXmacs/texts/scratch/no_name_2.tm>>
  </collection>
</references>

<\auxiliary>
  <\collection>
    <\associate|toc>
      <vspace*|1fn><with|font-series|<quote|bold>|math-font-series|<quote|bold>|1<space|2spc>Calcolo
      costanti Polso Sferico> <datoms|<macro|x|<repeat|<arg|x>|<with|font-series|medium|<with|font-size|1|<space|0.2fn>.<space|0.2fn>>>>>|<htab|5mm>>
      <no-break><pageref|auto-1><vspace|0.5fn>
    </associate>
  </collection>
</auxiliary>