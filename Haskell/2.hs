alooParatha :: Int -> Int -> Int -> Int
alooParatha cash cashPrice tokenPrice = alooParathaUtil cash 0 cashPrice tokenPrice


alooParathaUtil :: Int -> Int ->Int -> Int -> Int
alooParathaUtil cash token cashPrice tokenPrice
    |   cash >= cashPrice = (alooParathaUtil (cash-cashPrice) (token+1) cashPrice tokenPrice) + 1
    |   token >= tokenPrice = (alooParathaUtil cash (token-tokenPrice+1) cashPrice tokenPrice) + 1
    |   otherwise = 0
