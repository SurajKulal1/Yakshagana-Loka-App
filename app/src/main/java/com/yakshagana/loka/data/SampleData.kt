package com.yakshagana.loka.data

import com.yakshagana.loka.R
import com.yakshagana.loka.model.Artist
import com.yakshagana.loka.model.AudioClip
import com.yakshagana.loka.model.Event
import com.yakshagana.loka.model.VeshaArtistProfile
import com.yakshagana.loka.model.VeshaProfile
import java.time.LocalDateTime

object SampleData {
    val events = listOf(
        Event(
            id = "event2",
            melaName = "Dharmasthala Mela",
            title = "Karna Arjuna",
            venue = "Mangaluru Stage",
            dateTime = LocalDateTime.now().plusDays(1).withHour(20).withMinute(0),
            latitude = 12.9141,
            longitude = 74.8560,
            thumbnailUri = "yakshagana_slide_2",
            briefDescription = "An epic battle between Karna and Arjuna showcasing the finest Yakshagana martial arts and dramatic expressions.",
            contactInfo = "+91 9876543210 | dharmasthala@example.com",
            description = "This spectacular Yakshagana performance brings to life the legendary battle between Karna and Arjuna from the Mahabharata. Witness the finest martial arts, dramatic expressions, and traditional storytelling that has been passed down through generations. The performance features elaborate costumes, traditional makeup, and live music that creates an immersive cultural experience."
        )
    )

    val artists = listOf(
        Artist(
            id = "artist1",
            name = "Raghavendra Bhagavata",
            role = "Bhagavata",
            bio = "Renowned singer known for expressive rendering of prasangas.",
            veshas = listOf("Kaurava", "Raja", "Kirata"),
            gallery = emptyList()
        ),
        Artist(
            id = "artist2",
            name = "Mahabala Rao",
            role = "Actor",
            bio = "Celebrated stage artist known for powerful heroic roles.",
            veshas = listOf("Bheema", "Arjuna", "Ravana"),
            gallery = emptyList()
        )
    )

    val veshaProfiles = listOf(
        VeshaProfile(
            id = "narasimha",
            name = "Narasimha Vesham",
            description = "Embodies Lord Narasimha, the fierce half-man half-lion avatar of Vishnu, with explosive energy and devotional intensity.",
            characteristics = listOf("Fierce face paint with fangs", "Heavy headgear", "High-stamina physical acting"),
            significance = "A climactic vesha used in powerful dharmic moments where divine justice is shown through intense body language and voice.",
            performanceContexts = listOf("Bhagavata prasangas on Narasimha", "Temple festival night performances"),
            famousMelas = listOf("Kateel Mela", "Mandarthi Mela", "Perdooru Mela"),
            artists = listOf(
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Badagutittu, Uttara Kannada",
                    "Chittani, Uttara Kannada",
                    "One of the most influential Badagutittu actors of the 20th century. His Narasimha and other fierce veshas were studied for explosive energy, precise footwork, and command of the open-air stage.",
                    listOf("Long career with Idagunji Mahaganapati Yakshagana Mandali and other Uttara Kannada troupes", "Often cited in writing on Yakshagana for redefining intensity in demon and divine roles")
                ),
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Mythological actor, Honnavar tradition",
                    "Honnavar, Uttara Kannada",
                    "From the Keremane family line closely tied to the Idagunji mela. Known for layered mythological roles where posture and voice carry the narrative without excess movement.",
                    listOf("Sangeet Natak Akademi awardee from the 1970s generation of coastal performers", "Associated with recordings and tours that took coastal Yakshagana to wider audiences")
                ),
                vArtist(
                    "Keremane Shambhu Hegde",
                    "Actor and organiser, Honnavar",
                    "Keremane, Uttara Kannada",
                    "Led and shaped the Idagunji troupe's modern identity. Although celebrated for many veshas, his work in high-tension mythological sequences set a benchmark for how climax moments are paced.",
                    listOf("Former president of Karnataka Janapada Yakshagana Academy", "Toured internationally with the family mela, influencing how Narasimha-style sequences are staged abroad")
                ),
                vArtist(
                    "Kalinga Navada",
                    "Performer and guru, Kundapura belt",
                    "Kundapura, Udupi district",
                    "Known as both performer and trainer in the Kundapura and Baindur circuit. He emphasises breath control and eye discipline for roles that alternate between lyrical and ferocious beats.",
                    listOf("Runs workshops for students entering professional melas", "Frequently invited for demon and semi divine roles in coastal prasangas")
                ),
                vArtist(
                    "Hosanagara Ramachandra",
                    "Character actor, Malenadu link",
                    "Hosanagara, Shivamogga district",
                    "Bridged Shivamogga-region training with coastal touring. Valued for clear diction in sabha-style exchanges that often precede or follow a Narasimha sequence.",
                    listOf("Known for court and minister roles that frame mythological conflict", "Respected by melas for reliable long-season touring discipline")
                )
            ),
            imageRes = R.drawable.narasimha_vesha
        ),
        VeshaProfile(
            id = "duryodhana",
            name = "Kaurava and Duryodhana Vesham",
            description = "Portrays the proud and politically sharp Kaurava king from Mahabharata through authority-heavy acting.",
            characteristics = listOf("Royal crown and ornamentation", "Bold facial makeup", "Dialogue-dominant dramatic scenes"),
            significance = "A benchmark vesha for dialogue control, ego expression, and courtroom-style confrontation sequences.",
            performanceContexts = listOf("Mahabharata war-cycle prasangas", "Court debate scenes"),
            famousMelas = listOf("Dharmasthala Mela", "Saligrama Mela", "Hiriyadka Mela"),
            artists = listOf(
                vArtist(
                    "Keremane Mahabala Hegde",
                    "Kaurava specialist, Keremane school",
                    "Honnavar, Uttara Kannada",
                    "Long associated with the Idagunji mela Mahabharata cycle. His Duryodhana was noted for controlled arrogance, tight hand gestures, measured walk, and sabha dialogue that carries over chende and maddale peaks.",
                    listOf("Sangeet Natak Akademi recognition in the 1990s touring generation", "Mentored younger actors in how to sustain a royal register through a full night")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Badagutittu, Chittani",
                    "Uttara Kannada",
                    "While famous for ferocious veshas, his Kaurava-side roles are remembered for sudden shifts from charm to threat, useful in episodes where Duryodhana manipulates the sabha.",
                    listOf("Reference recordings for students learning Badagutittu dialogue timing", "Often paired with senior Bhagavatas for politically dense prasangas")
                ),
                vArtist(
                    "Hosanagara Ramachandra",
                    "Character and antagonist roles",
                    "Shivamogga hinterland",
                    "Known for villains and ministers who orbit the Kaurava court. Strong voice projection and clear Kannada make long dialogue blocks easier for audiences to follow.",
                    listOf("Regular in melas that stage Sabha Parva episodes", "Coaches younger artists in eye-line discipline during multi-actor sabha blocks")
                ),
                vArtist(
                    "Kumble Sundara Rao",
                    "Heroic and royal line, Udupi",
                    "Udupi district",
                    "Thenkutittu exposure gives a slightly different gait and ornament handling. When he takes Kaurava-related royal veshas, the contrast with pure Badaga style is instructive for students.",
                    listOf("Associated with Udupi-region professional melas", "Known for clean transitions between dialogue and choreographed entry sequences")
                ),
                vArtist(
                    "Nebburu Narayana Bhat",
                    "Senior Bhagavata ensemble partner",
                    "Coastal Karnataka",
                    "Though a Bhagavata, his work with Duryodhana-heavy prasangas matters. He sets harmonic tension so the actor's courtroom lines land. Many Kaurava specialists cite him as a preferred narrative partner.",
                    listOf("Known for long-season tours with major melas", "Documented in local press for sustaining sahitya clarity through dawn")
                )
            ),
            imageRes = R.drawable.duryodhana_vesha
        ),
        VeshaProfile(
            id = "bheema",
            name = "Bheema Vesham",
            description = "Represents strength, loyalty, and warrior courage through forceful movement vocabulary.",
            characteristics = listOf("Muscular body language", "Powerful voice projection", "Action-oriented movement"),
            significance = "A heroic vesha requiring both physical force and emotional grounding in family and dharma themes.",
            performanceContexts = listOf("Mahabharata heroic combats", "Vows and revenge episodes"),
            famousMelas = listOf("Dharmasthala Mela", "Kateel Mela", "Sode Mela"),
            artists = listOf(
                vArtist(
                    "Kumble Sundara Rao",
                    "Heroic lead, Udupi",
                    "Udupi district",
                    "Closely identified with Thenkutittu heroic line. His Bheema is built from low stance, wide shoulders in costume, and bursts of movement timed to maddale patterns.",
                    listOf("Long-time figure in Karnataka Yakshagana Academy activities, including chair and office roles reported in the press", "Mentors students on how to avoid empty aggression in fight episodes")
                ),
                vArtist(
                    "Udupi Rajagopala Acharya",
                    "Classical heroic actor",
                    "Udupi",
                    "Known for disciplined delivery of oath and vow sequences. He emphasises that Bheema's strength must read as duty-bound, not merely athletic.",
                    listOf("Associated with temple-season melas in Udupi cluster", "Teaches younger actors to align gait with tala cycles")
                ),
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Mythological warrior roles",
                    "Honnavar",
                    "From the Keremane performance tradition. His heroic roles are studied for how stillness between blows creates narrative weight.",
                    listOf("Recorded and toured with the Idagunji troupe", "Cited in academic writing on coastal Yakshagana style")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Badagutittu power roles",
                    "Uttara Kannada",
                    "When he played heroic veshas, the physical vocabulary was maximalist, useful reference for how Bheema can dominate space in open-air stages.",
                    listOf("Influenced a generation of northern-style actors", "Known for stamina across overnight performances")
                ),
                vArtist(
                    "Kalinga Navada",
                    "Performer and coach, Kundapura",
                    "Kundapura",
                    "Trains students in weapon-entry choreography and in how to recover voice after combat episodes. Often called for Bheema adjacent roles in ensemble casts.",
                    listOf("Runs residential-style coaching camps", "Bridges northern and southern coastal touring circuits")
                )
            ),
            imageRes = R.drawable.bheema_vesha
        ),
        VeshaProfile(
            id = "bhagavata",
            name = "Bhagavata singer role",
            description = "The lead vocalist-narrator who drives tempo, emotion, and story progression for the entire performance.",
            characteristics = listOf("Leads from orchestra line", "Controls rhythm and narrative pace", "High command of sahitya and raga flow"),
            significance = "Bhagavata is the narrative anchor. Performance quality depends on vocal authority and improvisational sensitivity.",
            performanceContexts = listOf("All full-night Yakshagana performances", "Interactive audience moments"),
            famousMelas = listOf("Saligrama Mela", "Mandarthi Mela", "Perdooru Mela"),
            artists = listOf(
                vArtist(
                    "Udyavara Madhava Acharya",
                    "Senior Bhagavata, Udupi",
                    "Udyavara, Udupi",
                    "Representative of the Udupi-line Bhagavata school. Known for lucid sahitya and steady pacing across prasangas that stretch past midnight.",
                    listOf("Associated with Karnataka Janapada Yakshagana Academy honours in news coverage", "Mentors younger Bhagavatas in episode transitions")
                ),
                vArtist(
                    "Puttige Raghuram Holla",
                    "Bhagavata, Puttige tradition",
                    "Puttige, Udupi",
                    "From a family name closely tied to Thenkutittu performance culture. His work illustrates how the Bhagavata conducts the night, cueing actors and shaping emotional arcs.",
                    listOf("Regular in major coastal melas", "Known for audience engagement during humorous interludes")
                ),
                vArtist(
                    "Nebburu Narayana Bhat",
                    "Bhagavata, Nebburu",
                    "Kasaragod and Dakshina Kannada belt",
                    "Known for carrying long narrative passages with minimal fatigue. Voice placement suited to open fields and temple compounds.",
                    listOf("Preferred partner for myth-heavy prasangas", "Documented in regional media for multi-decade touring")
                ),
                vArtist(
                    "Bannanje Sanjeeva Suvarna",
                    "Bhagavata and scholar voice",
                    "Bannanje, Udupi",
                    "Combines textual clarity with melodic variation. Often referenced when discussing clean enunciation in modern Thenkutittu.",
                    listOf("Associated with educational initiatives around Yakshagana", "Invited for lecture-demonstrations outside pure performance seasons")
                ),
                vArtist(
                    "Gode Narayana Hegde",
                    "Bhagavata, northern coastal",
                    "Uttara Kannada",
                    "Represents the northern coastal timbre and narrative habits. A useful contrast for students comparing Badaga and Tenku Bhagavata approaches.",
                    listOf("Long association with professional melas in Uttara Kannada", "Known for harmonium-supported phrasing choices in touring troupes")
                )
            ),
            imageRes = R.drawable.bhagavata_vesha
        ),
        VeshaProfile(
            id = "rakshasa",
            name = "Rakshasa Vesham",
            description = "Features demon archetypes with high theatrical exaggeration, vocal aggression, and dramatic visual structure.",
            characteristics = listOf("Large headgear", "Red and black makeup palette", "Loud and forceful acting style"),
            significance = "Essential for conflict intensity. It creates dramatic contrast against satvic and heroic characters.",
            performanceContexts = listOf("Demon-war narratives", "Mythological confrontation scenes"),
            famousMelas = listOf("Kateel Mela", "Dharmasthala Mela", "Kudlu Mela"),
            artists = listOf(
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Fierce-role benchmark",
                    "Uttara Kannada",
                    "His rakshasa and related demon veshas are still used as teaching clips: sudden vocal spikes, controlled chaos in crown work, and clear silhouette against flame light.",
                    listOf("National and state-level recognition in cultural press", "Influenced makeup and movement for a generation")
                ),
                vArtist(
                    "Keremane Mahabala Hegde",
                    "Power antagonist line",
                    "Honnavar",
                    "Known for antagonists where ego reads physically, shoulders squared to crown height. Pairs well with rakshasa episodes that pivot to sabha politics.",
                    listOf("Idagunji mela association", "Mentors younger actors in sustaining volume without damaging voice")
                ),
                vArtist(
                    "Kalinga Navada",
                    "Demon roles, training focus",
                    "Kundapura",
                    "Runs exercises for wrist and crown coordination and for landing jumps without breaking character. His demon work stresses repeatability across a long season.",
                    listOf("Invited by youth melas for workshop segments", "Known for pairing comic beats with menace in composite episodes")
                ),
                vArtist(
                    "Hosanagara Ramachandra",
                    "Secondary demons and ministers",
                    "Shivamogga",
                    "Often cast in roles that orbit the main rakshasa, messengers, lesser generals, where timing with the Bhagavata is critical.",
                    listOf("Reliable for double-cast nights", "Teaches ensemble awareness in fight choreography")
                ),
                vArtist(
                    "Keremane Shambhu Hegde",
                    "Full-range myth actor",
                    "Honnavar",
                    "While not only a demon specialist, his performances in high-contrast myth episodes show how a lead actor can swing between satvic and terrifying registers.",
                    listOf("International touring exposure with Idagunji troupe", "Widely quoted in obituaries and profiles as a modern Yakshagana architect")
                )
            ),
            imageRes = R.drawable.rakshasa_vesha
        ),
        VeshaProfile(
            id = "kattige",
            name = "Kattige (Heroic Warrior)",
            description = "Represents noble warrior archetypes that demand dignified speech and balanced kinetic acting.",
            characteristics = listOf("Elegant costume line", "Balanced acting and dialogue", "Disciplined martial stance"),
            significance = "A classical heroic frame that communicates courage, ethics, and leadership under pressure.",
            performanceContexts = listOf("Battlefield and oath scenes", "Royal command episodes"),
            famousMelas = listOf("Saligrama Mela", "Perdooru Mela", "Hiriyadka Mela"),
            artists = listOf(
                vArtist(
                    "Keremane Shambhu Hegde",
                    "Kattige and heroic benchmark",
                    "Honnavar",
                    "Often discussed as a complete system actor where voice, gait, and emotional arc stay integrated. His kattige-style heroes read as leaders under moral pressure, not only fighters.",
                    listOf("Led the Idagunji troupe's pedagogical outreach", "Frequently cited in profiles as a bridge between traditional rigour and modern stage design")
                ),
                vArtist(
                    "Kumble Sundara Rao",
                    "Thenkutittu heroic dignity",
                    "Udupi",
                    "Emphasises upright carriage and minimalistic gesture for noble warriors. A good reference for how kattige differs from maximalist demon movement.",
                    listOf("Academy-level involvement in coastal Yakshagana policy discussions", "Mentors university-affiliated workshops in the region")
                ),
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Classical line, Honnavar",
                    "Honnavar",
                    "Known for mythological heroes where dialogue weight equals movement. Students study his transitions from counsel scenes to battlefield resolve.",
                    listOf("Sangeet Natak Akademi honour", "Recorded legacy with family mela")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Badagutittu heroic contrast",
                    "Uttara Kannada",
                    "When he played heroes, the same intensity used in demons became controlled fire, a different lesson in kattige than softer southern variants.",
                    listOf("Cross mela appearances documented in regional histories", "Influenced northern heroic costuming choices")
                ),
                vArtist(
                    "Udupi Rajagopala Acharya",
                    "Heroic roles, Udupi",
                    "Udupi",
                    "Known for vow and oath episodes where the hero must sound resolved without shouting. Pairs well with Bhagavatas who favour slower narrative builds.",
                    listOf("Temple-season regular", "Teaches younger actors in college-affiliated troupes")
                )
            ),
            imageRes = R.drawable.kattige_vesha
        ),
        VeshaProfile(
            id = "stree",
            name = "Stree Vesham female role",
            description = "Depicts female characters through graceful movement, emotional nuance, and controlled facial expression.",
            characteristics = listOf("Graceful kinetics", "Subtle expression design", "Voice-emotion synchronization"),
            significance = "Demands high finesse and restraint, often becoming a test of artistic maturity in classical staging.",
            performanceContexts = listOf("Mythological queen and princess roles", "Emotion-heavy family and court scenes"),
            famousMelas = listOf("Mandarthi Mela", "Kateel Mela", "Dharmasthala Mela"),
            artists = listOf(
                vArtist(
                    "Keremane Shambhu Hegde",
                    "Stree vesham, male tradition",
                    "Honnavar",
                    "Historically, male performers carried most stree roles. Shambhu Hegde's work is often highlighted for emotional nuance, small facial shifts and controlled hip lines rather than broad caricature.",
                    listOf("Profiled widely after his death as a reference for modern stree grammar", "Toured internationally, showing stree sequences to diaspora audiences")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Versatile performer including stree range",
                    "Uttara Kannada",
                    "Demonstrated that the same performer could swing between ferocious and lyrical. His stree-related work is studied for how power is softened without losing presence.",
                    listOf("Cross-gender role versatility noted in academic Yakshagana literature", "Influenced younger actors in Uttara Kannada training lineages")
                ),
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Emotion-led myth roles",
                    "Honnavar",
                    "Known for queen and maternal roles where the text is heavy. Voice stays in a mid register to preserve longevity through the night.",
                    listOf("Sangeet Natak Akademi context", "Part of Keremane pedagogical lineage")
                ),
                vArtist(
                    "Kumble Sundara Rao",
                    "Thenkutittu lyrical heroes and consorts",
                    "Udupi",
                    "While primarily known for heroic male veshas, his training workshops discuss how soft power gestures translate to consort and court scenes.",
                    listOf("Engages with mixed-gender troupes in the modern era", "Advocates for measured ornament weight in stree costuming")
                ),
                vArtist(
                    "Kalinga Navada",
                    "Movement coach, grace roles",
                    "Kundapura",
                    "Teaches eye and neck isolation drills borrowed from stree grammar even to male heroic students, cross-training that improves overall stage presence.",
                    listOf("Youth mela workshops", "Documented in local news for women's participation initiatives alongside classical training")
                )
            ),
            imageRes = R.drawable.stree_vesha
        ),
        VeshaProfile(
            id = "ravana",
            name = "Ravana Vesham",
            description = "Portrays the complex king of Lanka with grandeur, intellect, ego, and tragic depth.",
            characteristics = listOf("Grand costume and regalia", "Powerful dialogue arcs", "Villain with emotional layers"),
            significance = "A high-value role for demonstrating authority, scholarship, and dramatic downfall in one character arc.",
            performanceContexts = listOf("Ramayana conflict episodes", "Court and war strategy scenes"),
            famousMelas = listOf("Sode Mela", "Saligrama Mela", "Kumbashi Mela"),
            artists = listOf(
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Ravana and layered king roles",
                    "Honnavar",
                    "Often highlighted for Ravana portrayals where scholarship and ego are visible in the same breath, hand mudra for vidwan pride, voice drop for isolation scenes.",
                    listOf("Sangeet Natak Akademi awardee", "Recorded reference for Ramayana prasangas in coastal style")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Grand antagonist scale",
                    "Uttara Kannada",
                    "His larger-than-life stage vocabulary suits Lanka court episodes. Students study how he fills the width of the rangasthala without losing dialogue clarity.",
                    listOf("Influential for crown-heavy roles", "Cited in demon and king role pedagogy alike")
                ),
                vArtist(
                    "Kumble Sundara Rao",
                    "Southern heroic contrast to Ravana",
                    "Udupi",
                    "Useful in double-cast nights where Rama and Ravana contrast is staged. His body grammar as order against Ravana excess is a teaching point.",
                    listOf("Thenkutittu mela circuit", "Academy-level figure in regional press")
                ),
                vArtist(
                    "Hosanagara Ramachandra",
                    "Minister and court orbit roles",
                    "Shivamogga",
                    "Frequently in Ravana's court as Mareecha, minister, or envoy, roles that require quick switches between flattery and fear.",
                    listOf("Supports main actors in long dialogue chains", "Known for consistent touring discipline")
                ),
                vArtist(
                    "Keremane Mahabala Hegde",
                    "Power hierarchy on stage",
                    "Honnavar",
                    "When cast in Ravana's family or rival king lines, his work shows how secondary crowns still read in depth if sabha blocking is precise.",
                    listOf("Idagunji mela heritage", "Mentors ensemble scenes around the throne")
                )
            ),
            imageRes = R.drawable.ravana_vesha
        ),
        VeshaProfile(
            id = "nagakanya",
            name = "Nagakanya Vesham",
            description = "Represents serpent-princess mythology with dance-led storytelling and mystical emotional tone.",
            characteristics = listOf("Grace-focused choreography", "Mythical visual symbolism", "Expressive eye movement and mudra use"),
            significance = "Aesthetic-heavy vesha that blends dance and drama, often used to shift tone into lyrical mythic space.",
            performanceContexts = listOf("Mythological transformation narratives", "Serpent-lore episodes"),
            famousMelas = listOf("Mandarthi Mela", "Perdooru Mela", "Kundapura circuit troupes"),
            artists = listOf(
                vArtist(
                    "Kalinga Navada",
                    "Grace and nagakanya coaching",
                    "Kundapura",
                    "Centres training on circular gait patterns and slow neck turns suited to serpent-lore episodes. Links breath to flute and vocal cues from the Bhagavata.",
                    listOf("Residential camps for youth troupes", "Cross-trains Bharatanatyam-aware students entering Yakshagana")
                ),
                vArtist(
                    "Keremane Shambhu Hegde",
                    "Lyric and myth transitions",
                    "Honnavar",
                    "Known for episodes where tone shifts from war to dream-like sequences. Nagakanya-type staging benefits from his sense of pause before spectacle.",
                    listOf("International demonstration tours", "Quoted in cultural journalism on full-night pacing")
                ),
                vArtist(
                    "Kumble Sundara Rao",
                    "Southern lyrical line",
                    "Udupi",
                    "Thenkutittu ornamentation choices for female myth roles differ slightly from Badaga. His workshops compare hip line and veil work for students.",
                    listOf("Udupi mela cluster regular", "Collaborates with composers on new sahitya for lyrical inserts")
                ),
                vArtist(
                    "Udupi Rajagopala Acharya",
                    "Supporting myth roles",
                    "Udupi",
                    "Often in father and sage roles that frame nagakanya narratives. His stillness on stage lets the dance-heavy vesha read more clearly.",
                    listOf("Temple-season favourite", "Mentors duet timing with Bhagavata")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Contrast: power to lyricism",
                    "Uttara Kannada",
                    "Even when not primarily a nagakanya specialist, his ability to drop intensity informs how power characters react to serpent-princess sequences.",
                    listOf("Pedagogical example in style-comparison classes", "Historical recordings still circulated among students")
                )
            ),
            imageRes = R.drawable.nagakanya_vesha
        ),
        VeshaProfile(
            id = "arjuna",
            name = "Arjuna Vesham",
            description = "Depicts the skilled warrior prince known for discipline, precision, and moral conflict in Mahabharata.",
            characteristics = listOf("Elegant acting grammar", "Archery symbolism", "Composed heroic demeanor"),
            significance = "A principal heroic vesha balancing technique, nobility, and internal emotional conflict.",
            performanceContexts = listOf("Mahabharata archery and vow episodes", "Krishna-Arjuna ethical dialogues"),
            famousMelas = listOf("Dharmasthala Mela", "Hiriyadka Mela", "Kateel Mela"),
            artists = listOf(
                vArtist(
                    "Kumble Sundara Rao",
                    "Arjuna and heroic precision",
                    "Udupi",
                    "Frequently referenced for Arjuna-style composure: narrow stance, controlled bow gestures, and dialogue that sounds like strategy, not anger.",
                    listOf("Thenkutittu professional melas", "Workshops on Krishna and Arjuna episode timing")
                ),
                vArtist(
                    "Keremane Shivarama Hegde",
                    "Mythological prince line",
                    "Honnavar",
                    "His Arjuna-related work shows northern coastal ornament handling and how crown weight changes turning patterns during archery mime.",
                    listOf("Sangeet Natak Akademi context", "Recorded prasanga excerpts used in colleges")
                ),
                vArtist(
                    "Udupi Rajagopala Acharya",
                    "Dialogue-heavy Arjuna beats",
                    "Udupi",
                    "Strong in episodes where Arjuna debates dharma with Krishna. Voice stays forward without pushing pitch.",
                    listOf("Mentors MAHE and regional university collaborations", "Temple melas around Udupi")
                ),
                vArtist(
                    "Chittani Ramachandra Hegde",
                    "Contrast study: discipline vs fury",
                    "Uttara Kannada",
                    "Used in teaching to show how the same body can be contained for Arjuna after playing wilder veshas, valuable for student control work.",
                    listOf("Historical benchmark for coastal style", "Cross-referenced in style manuals")
                ),
                vArtist(
                    "Hosanagara Ramachandra",
                    "Peer warrior and rival roles",
                    "Shivamogga",
                    "Often in Karna-side or allied prince roles that mirror Arjuna's posture language, useful for choreographed face-offs.",
                    listOf("Touring partner for split-cast nights", "Known for sabha argument blocking")
                )
            ),
            imageRes = R.drawable.arjuna_vesha
        )
    )

    val audioClips = listOf(
        AudioClip(
            id = "song1",
            title = "Enthaha Cheluvu",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Enthaha%20Cheluvu.mp3"
        ),
        AudioClip(
            id = "song2",
            title = "Ganapathi Sthuthi",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Ganapathi%20Sthuthi.mp3"
        ),
        AudioClip(
            id = "song3",
            title = "Harivaraasanam",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Harivaraasanam.mp3"
        ),
        AudioClip(
            id = "song4",
            title = "Kari Mugila Baaninalli",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Kari%20Mugila%20Baaninalli.mp3"
        ),
        AudioClip(
            id = "song5",
            title = "Maralu Madikondeyalla",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Maralu%20Madikondeyalla.mp3"
        ),
        AudioClip(
            id = "song6",
            title = "Mudadinda Ninna",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Mudadinda%20Ninna.mp3"
        ),
        AudioClip(
            id = "song7",
            title = "Munisu Tharave Mugudhe",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Munisu%20Tharave%20Mugudhe.mp3"
        ),
        AudioClip(
            id = "song8",
            title = "Neela Gaganadholu",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Neela%20Gaganadholu.mp3"
        ),
        AudioClip(
            id = "song9",
            title = "Nirata Vaadidharu",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/Nirata%20Vaadidharu.mp3"
        ),
        AudioClip(
            id = "song10",
            title = "taani tandana",
            artistName = "Yakshagana",
            url = "https://raw.githubusercontent.com/SurajKulal1/yakshagana-radio-assets/main/radio/songs/taani%20tandana.mp3"
        )
    )
}

private fun vArtist(
    name: String,
    stageTitle: String,
    place: String,
    styleNote: String,
    achievements: List<String>
): VeshaArtistProfile {
    return VeshaArtistProfile(
        name = name,
        stageTitle = stageTitle,
        place = place,
        styleNote = styleNote,
        achievements = achievements
    )
}
