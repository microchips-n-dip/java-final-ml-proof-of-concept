The structure of the program is fundamentally similar to the original: it is subdivided into
environments (roughly equivalent to screens) each responsible for the state of some part of the
game. The main difference is the functionality of user actions being abstracted to Action functions,
implemented as descendents of the class Action (since Java doesn't treat functions as first-class).
Each action acts on its environment; let S be the set of possible states for environment E, then
action A is a function A : S -> S.

The machine learning portion of the code consists of two main parts: policy and training. A policy
is a function P of the form P : S -> A* where A* is the set of possible actions. It merely selects
whichever action A in A* satisfies A(s) >= A'(s) with s in S and A' any action in A*. The reason
to use machine learning is because we do not know which outcomes are preferable, and so we assume
some target preference Q(s1, a1) < Q(s2, a2) < ... < Q(sn, an) where Q(s, a) represents the
expected reward of a(s), that is R(a(s)). Since we do not know this function Q(s, a), we approximate
it by a family of functions Q'i(a, s) for each i. Each training step corresponds to taking the
function at i + 1 in this family.
