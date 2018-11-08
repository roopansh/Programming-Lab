{-
    Aloo paratha
    @param cash to start with
    @param cashprice price of 1 aloo paratha in cash
    @param tokenprice price of 1 aloo paratha in tokens
    @returns the maximum number of aloo paratha one can eat in the shop

    calls alooparathautil with 0 tokens initially
-}
alooParatha :: Int -> Int -> Int -> Int
alooParatha cash cashPrice tokenPrice = alooParathaUtil cash 0 cashPrice tokenPrice


{-
    Aloo paratha util
    used to store the number of tokens with the customer as well
    @param cash with the customer
    @param token with the customer
    @param cashprice price of 1 aloo paratha in cash
    @param tokenprice price of 1 aloo paratha in tokens
    @returns the maximum number of aloo paratha one can eat in the shop
-}
alooParathaUtil :: Int -> Int ->Int -> Int -> Int
alooParathaUtil cash token cashPrice tokenPrice
    -- if the user can buy using cash, buy 1 paratha and get 1 token
    |   cash >= cashPrice = (alooParathaUtil (cash-cashPrice) (token+1) cashPrice tokenPrice) + 1
    -- if the user can't buy using cash but token, buy 1 paratha and get 1 token
    |   token >= tokenPrice = (alooParathaUtil cash (token-tokenPrice+1) cashPrice tokenPrice) + 1
    -- if the user can't buy using cash or token, return 0 (base condition of the recursion)
    |   otherwise = 0
