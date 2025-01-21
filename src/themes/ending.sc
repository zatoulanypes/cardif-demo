theme: /

    state: Ending
        q!: PaymentMade
        script:
            $analytics.setSessionResult("Payment made");
            $analytics.setSessionData("The user made the payment", "Yes");
        a: I have received the confirmation of your payment! 🎉🥳🎊
            Now your home is protected. 🏠✨
        go!: /NPS
    
    state: NPS
        script:
            $analytics.setSessionResult("Ask for feedback");
        random:
            a: Please tell me if our conversation was pleasant for you? 🤗
            a: One more question: was it a pleasure talking to me? 😊
        buttons:
            "Yes! 🤩" -> ./Yes
            "No ☹️" -> ./No

        state: Yes
            q: * $localAgree *
            script:
                $analytics.setSessionResult("The user enjoyed interacting with the bot");
                $analytics.setSessionData("NPS", "Positive");
            a: Wow, thanks a lot for your feedback! ♥
            go!: /Goodbye
        
        state: No
            q: * $localDisagree *
            script:
                $analytics.setSessionResult("The user didn't enjoy interacting with the bot");
                $analytics.setSessionData("NPS", "Negative");
            a: Sorry about that. I will definetly work on it. 😔
            go!: /Goodbye
        
        state: CatchAll || noContext = true
            event: noMatch
            script:
                $analytics.setSessionResult("It is not known whether the user not really enjoyed interacting with the bot");
                $analytics.setSessionData("NPS", "Don't know");
            a: Thanks for your feedback! 🙏
            go!: /Goodbye
