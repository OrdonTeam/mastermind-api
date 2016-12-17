import groovy.json.JsonSlurper

"http://127.0.0.1:8080/mastermind/${UUID.randomUUID()}?guess[]=".with { gameUrl ->
    System.in.eachLine { guess ->
        new JsonSlurper()
                .parse(new URL(gameUrl + guess))
                .moves
                .last()
                .with { move ->
            println("${move.match.positionMatch} ${move.match.colorMatch}")
        }
    }
}