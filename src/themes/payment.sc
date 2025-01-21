theme: /Payment

    state: GenerateLink
        script:
            $analytics.setSessionResult("Payment link generation");
            $session.paymentLink = paymentApi.getPaymentLink($session.user.documentNumber);
        if: $session.paymentLink
            go!: /Payment/PaymentLink
        else:
            go!: ./LocalApiError
        
        state: LocalApiError
            script:
                $analytics.setSessionResult("Payment link generation ERROR");
            if: countRepeatsInRow() < 2
                go!: /Payment/GenerateLink
            else:
                script:
                    $analytics.setSessionData("Payment link generation ERROR", "Yes");
                a: Sorry about that, we're having a bit of trouble with an external service. Our team is on it, and they're working to fix it.🔧
                go!: /ContactAgent
    
    state: PaymentLink
        script:
            $analytics.setSessionResult("Payment link is sent");
        a: Here's the link to make your payment: [Click here]({{ $session.paymentLink }})

            You will be directed to a security page to pay and after that we will receive the confirmation to validate you home insurance. 🔒
        a: Just let me know if anything goes wrong with the payment. I'll be here and wait until it's done. ⏳
        buttons:
            "There's a trouble" -> ./CantPay
        q: PaymentMade || toState = "/Ending"

        state: CantPay
            q: $cantPay
            a: If you've got any questions, worries, or run into any problems, I suggest you contact your nearest a remote sales agent. You can find one via this [website](https://example.com). 🌐
            go!: /Goodbye
