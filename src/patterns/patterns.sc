patterns:
    $Surname = $oneWord || converter = function(parseTree) { return capitalize(parseTree.text); };
    $localDisagree = (no/disagree) *
    $localAgree = (yes/ok) *
    $correct = (correct/all right) *
    $incorrect = (incorrect/wrong/not my) *
    $cantPay = * (cant/cannot) pay* *
