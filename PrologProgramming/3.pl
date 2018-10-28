% Read the input file
% Encode the input
% Write back to the output file
q3:-
    open('C:/users/Roopansh Bansal/Desktop/input.txt', read, ReadStream),
    read(ReadStream, Input),
    close(ReadStream),
    string_chars(Input, InputList),
    write(Input),nl,
    encode_q3(InputList, Output),
    write(Output),
    open('C:/users/Roopansh Bansal/Desktop/output.txt', write, WriteStream),
    write(WriteStream, Output),
    close(WriteStream)
    .

% Encodes the input and sets the output
% First group the same consecutive characters
% Then count the length of such sublists and replace by the count
% Check and remove the single count subsets
encode_q3(Input, Output) :-
    group_q3(Input, Grouped),
    count_q3(Grouped, CountSeq),
    singleton_q3(CountSeq, Output)
    .

% If input is empty, then output is empty
group_q3([], []).
% Check the first character of the input.
% Group the sequence, add to the result & find out the remaining string
% Recurse into the remaning string
group_q3([InputHead | InputTail], [Result | ResultTail]):-
    group_util(InputHead, InputTail, Remaining, Result),
    group_q3(Remaining, ResultTail)
    .

% If the rest of the input is empty and only the character is left, then
% output that character as a singleton and empty remaning string
group_util(Char,[],[],[Char]).
% If the character being grouped is not equal the the first character,
% then stop here and return the remaining characters
group_util(Char,[Y|YTail],[Y|YTail],[Char]) :-
    Char \= Y
    .
% If the character is same as the next character, group them and recurse
% into the remaning string
group_util(Char,[Char|Tail],Remaining,[Char|Zs]) :-
    group_util(Char,Tail,Remaining,Zs)
    .

% If empty input, then empty output
count_q3([],[]).
% Count the lengths of the grouped subsets
% Output the length and the character
count_q3([[Char|CharList]|RemainingInput],[[Count,Char]|RemainingOutput]) :-
    length([Char|CharList], Count),
    count_q3(RemainingInput,RemainingOutput)
    .

% Remove the subsets with count 1
singleton_q3([], []).
singleton_q3([[1,Char]|RemainingInput], [Char|RemainingOutput]) :-
    singleton_q3(RemainingInput, RemainingOutput)
    .
singleton_q3([[Count,Char]| RemainingInput], [[Count, Char]|RemainingOutput]) :-
    Count > 1,
    singleton_q3(RemainingInput, RemainingOutput)
    .




