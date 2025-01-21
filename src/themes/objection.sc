theme: /Objection

    state: NotInterested
        script:
            $analytics.setScenarioAction("Objections");
        if: counter($context, "objectionCounter") < CATCH_ALL_LIMIT
            random: 
                a: Can you give me a reason why you'd rather not? 💭
                a: Why don't you want to? 💭
            buttons:
                "Too expensive"
                "Don't need"
                "I'm safe"
        else:
            random:
                a: I know you're thinking carefully about insurance, as it's a big decision. 💼
                a: I appreciate you're worried about getting the right insurance. 💼
            go!: /ContactAgent

        state: Return
            q: $localDisagree
            go!: {{ $session.lastContextState }}

        state: CatchAll || noContext = true
            event: noMatch
            script:
                $analytics.setSessionResult("Objections: unrecognized response");
                $temp.countRepeatsInRow = countRepeatsInRow();
            if: $temp.countRepeatsInRow === 1
                a: Unfortunately, I didn't get it. Tell me, why don't you want to renew insurance policy. 😣
            elseif: $temp.countRepeatsInRow === 2
                a: I'm sorry, but I still don't understand. What are the objections to not renewing insurance policy. 🔍
            else:
                a: I'm sorry, but I still couldn't understand what you meant. 😟

                go!: /ContactAgent
            
    state: TooExpensive
        intent!: /TooExpensive
        script:
            $session.objection = "Too expensive";
        go!: /Objection/Objection
            
    state: DontNeed
        intent!: /DontNeed
        script:
            $session.objection = "Don't need";
        go!: /Objection/Objection
            
    state: ImSafe
        intent!: /ImSafe
        script:
            $session.objection = "I'm safe";
        go!: /Objection/Objection
            
    state: OtherPriorities
        intent!: /OtherPriorities
        script:
            $session.objection = "Other priorities";
        go!: /Objection/Objection

    state: Objection
        script:
            $analytics.setSessionData("User's objection", $session.objection);
            if ($session.lastState === "/Objection/NotInterested") $session.counter["objectionCounter"] -= 1;
        if: counter("objectionCounter") < CATCH_ALL_LIMIT
            a: {{$Objections[$session.objection].answer}}
            go!: {{$session.lastContextState}}
        else:
            random:
                a: I know you're thinking carefully about insurance, as it's a big decision. 💼
                a: I appreciate you're worried about getting the right insurance. 💼
            go!: /ContactAgent
            
    state: CompanyInfo
        intent!: /CompanyInfo
        script:
            $session.question = "CompanyInfo";
        go!: /Objection/Question
            
    state: WhyIsDocumentRequired
        intent!: /WhyIsDocumentRequired
        script:
            $session.question = "WhyIsDocumentRequired";
        go!: /Objection/Question
            
    state: InfoSource
        intent!: /InfoSource
        script:
            $session.question = "InfoSource";
        go!: /Objection/Question
            
    state: WhyIsAddressRequired
        intent!: /WhyIsAddressRequired
        script:
            $session.question = "WhyIsAddressRequired";
        go!: /Objection/Question
            
    state: AreYouRobot
        intent!: /AreYouRobot
        script:
            $session.question = "AreYouRobot";
        go!: /Objection/Question

    state: Question
        a: {{$Questions[$session.question].answer}}
        script:
            $analytics.setSessionData("User's question", $session.question);
        go!: {{$session.lastContextState}}
        
    state: YouAreFrauds
        intent!: /AreYouFrauds
        script:
            $analytics.setSessionResult("User asks if this is a fraud");
            $analytics.setSessionData("Asked the user to contact an agent", "Yes");
        random:
        random:
            a: No, this isn't a scam. We understand that these things can be worrying. I suggest you contact your nearest a remote sales agent to continue discussing your insurance policy.
            a: No, this isn't a scam. We understand that these things can be worrying. I suggest you get in touch with your closest remote sales agent to continue the conversation about your insurance policy.
        go!: /Goodbye
        