prefix([], _).
prefix([H|T1], [H|T2]) :- prefix(T1, T2).

isSub(S1, S2) :-
    string_chars(S1, L1),
    string_chars(S2, L2),
    isSub_List(L1, L2).

isSub_List(S, L) :- prefix(S, L).

isSub_List(S, [_|T]) :- isSub_List(S, T).
