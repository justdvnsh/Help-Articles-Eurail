package com.thatmobiledevagency.helparticles.articles.data.network

import com.thatmobiledevagency.helparticles.articles.domain.Article
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val mockArticles = listOf(
    Article(
        id = "1",
        title = "How to use your Eurail Pass",
        summary = "Learn the basics of using your Eurail Pass for train travel across Europe. This guide covers activation, seat reservations, and travel days.",
        contentHtml = """
                <h1>How to use your Eurail Pass</h1>
                <p>Your Eurail Pass is your ticket to explore Europe by train. Here's everything you need to know:</p>
                <h2>Activating your Pass</h2>
                <p>Before your first journey, you need to activate your pass at a train station. Bring your passport and the pass itself.</p>
                <h2>Making Reservations</h2>
                <p>Some trains require seat reservations. You can make these online or at the station.</p>
                <h2>Travel Days</h2>
                <p>Remember to fill in your travel days before boarding the train. Use a pen and write the date clearly.</p>
            """.trimIndent(),
        updatedAt = Clock.System.now().toEpochMilliseconds() - (2 * 24 * 60 * 60 * 1000)
    ),
    Article(
        id = "2",
        title = "Seat Reservations Explained",
        summary = "Understanding when and how to make seat reservations for your Eurail journey. Some trains require reservations, others don't.",
        contentHtml = """
                <h1>Seat Reservations Explained</h1>
                <p>Not all trains require reservations, but some do. Here's what you need to know:</p>
                <h2>High-Speed Trains</h2>
                <p>Most high-speed trains like TGV, ICE, and Eurostar require reservations.</p>
                <h2>Regional Trains</h2>
                <p>Regional trains usually don't require reservations - just hop on with your pass!</p>
                <h2>How to Reserve</h2>
                <p>You can make reservations online, at train stations, or through travel agencies.</p>
            """.trimIndent(),
        updatedAt = Clock.System.now().toEpochMilliseconds() - (5 * 24 * 60 * 60 * 1000)
    ),
    Article(
        id = "3",
        title = "Traveling with Children",
        summary = "Everything you need to know about traveling with children using a Eurail Pass. Age requirements, discounts, and family-friendly tips.",
        contentHtml = """
                <h1>Traveling with Children</h1>
                <p>Traveling with kids? Here's what you need to know:</p>
                <h2>Age Requirements</h2>
                <p>Children under 4 travel free. Children 4-11 get a discounted pass.</p>
                <h2>Family Benefits</h2>
                <p>Some passes offer family discounts. Check the terms when purchasing.</p>
            """.trimIndent(),
        updatedAt = Clock.System.now().toEpochMilliseconds() - (7 * 24 * 60 * 60 * 1000)
    ),
    Article(
        id = "4",
        title = "Refund and Cancellation Policy",
        summary = "Learn about Eurail Pass refund and cancellation policies. Important information about changing or canceling your pass.",
        contentHtml = """
                <h1>Refund and Cancellation Policy</h1>
                <p>Need to cancel or change your pass? Here's our policy:</p>
                <h2>Refund Eligibility</h2>
                <p>Unused passes can be refunded within certain timeframes. See terms for details.</p>
                <h2>Processing Time</h2>
                <p>Refunds typically take 7-14 business days to process.</p>
            """.trimIndent(),
        updatedAt = Clock.System.now().toEpochMilliseconds() - (10 * 24 * 60 * 60 * 1000)
    ),
    Article(
        id = "5",
        title = "Popular Routes and Itineraries",
        summary = "Discover popular train routes and suggested itineraries for your European adventure. From Paris to Rome, Amsterdam to Berlin, and more.",
        contentHtml = """
                <h1>Popular Routes and Itineraries</h1>
                <p>Planning your route? Here are some popular options:</p>
                <h2>Classic Western Europe</h2>
                <p>Paris → Amsterdam → Berlin → Prague</p>
                <h2>Mediterranean Adventure</h2>
                <p>Barcelona → Nice → Rome → Florence</p>
                <h2>Scandinavian Journey</h2>
                <p>Copenhagen → Stockholm → Oslo</p>
            """.trimIndent(),
        updatedAt = Clock.System.now().toEpochMilliseconds() - (1 * 24 * 60 * 60 * 1000)
    )
)