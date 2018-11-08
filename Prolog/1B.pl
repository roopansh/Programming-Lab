% returns the longest increasing subsequence
lis(Input, Output) :-
	% Aggregate all the increasing subsequences(In reverse order)
	% Compare the lengths and store the max length subsequence in Result
	aggregate(max(Len,IncSub),
		  (one_is(Input, [], IncSub), length(IncSub, Len)),
		  max(_, Result)),
	% Reverse and output the result
	reverse(Result, Output)
	.


% find increasing subsequence
% If input becomes empty, then return the string in the
% current(accumulator)
one_is([], Current, Current).

one_is([H | T], Current, Final) :-
	% If the current(accumulator) is empty, push the head in accumulator
	% and recurse
	(   Current = [], one_is(T, [H], Final));
	% If current is not empty, then push the head of input if
	% it is larger than the head of the accumulator and recurse
	(   Current = [H1 | _], H1 < H,   one_is(T, [H | Current], Final));
	one_is(T, Current, Final).
