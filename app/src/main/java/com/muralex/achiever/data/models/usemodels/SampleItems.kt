package com.muralex.achiever.data.models.usemodels


class SampleItems {

    val IMAGES_FOLDER_ICONS =  "file:///android_asset/test/"


    private fun convertGroup(item: Item): GroupData {
        return GroupData(id = item.id, title = item.title, text = item.desc)
    }


    fun groups(): List<GroupData> {

        val list = mutableListOf<GroupData>()

        itemsList.forEach {
            list.add( convertGroup(it) )
        }

         repeat(4) {  list.addAll(list)  }

        return list
    }



    val itemsList = listOf(
        Item(
            "From St. Lucia to Morocco, find the locale that'll make your journey soar.",
            "Whether you're ready to hike in or Instagram the moment, odds are you'll enjoy experiencing the scenery at a new destination, especially if it's different from back home. While beauty is in the eye of the beholder, these 50 places are bound to take your breath away with their awe-inspiring settings.",
            IMAGES_FOLDER_ICONS + "landscapes/st_lucia.jpg",
            "1"
        ),
        Item(
            "Gower Peninsula: Swansea, Wales",
            "Crabbing, surfing, bicycling, walking and bird-watching are favorite pastimes on the beaches of the Gower Peninsula, one of Great Britain's designated Areas of Outstanding Natural Beauty. Rhossili Bay's white sand, saltwater and freshwater marshes, towering cliffs and sparkling blue water attract summer visitors in droves. Come at sunset to hear the gulls, blackcaps, warblers and goldcrests, and stay to watch the stars rise over the quiet beach.",
            IMAGES_FOLDER_ICONS + "landscapes/gower.jpg",
            "2"
        ),
        Item(
            "Vestrahorn: Iceland",
            "Nearly 1,500 feet tall, Vestrahorn mountain towers above a lagoon and black sand beach so dramatically that some travelers refer to it as Batman Mountain. Remoteness is one of the sight's greatest assets, so expect to walk after driving to the Stokksnes peninsula to get the most stunning views. On sunny days, reflections in the water lapping on the black sand shore accentuate the unusual peaks.",
            IMAGES_FOLDER_ICONS + "landscapes/vestrahorn.jpg",
            "3"
        ),
        Item(
            "The Pitons: Soufriere, St. Lucia",
            "These lush twin peaks rising straight from the Caribbean Sea are St. Lucia's most famous sights. Part of the UNESCO World Heritage-listed Pitons Management Area, the Pitons feature fumaroles, hot springs, petroglyphs and ample wildlife-viewing opportunities. At 2,619 feet, Gros Piton is the taller and easier of the two to climb, though Petit Piton looks nearly identical in height when seen from the north.",
            IMAGES_FOLDER_ICONS + "landscapes/pitons.jpg",
            "4"
        ),
        Item(
            "Denali National Park & Preserve: Alaska",
            "It's hard to miss this national park's centerpiece and namesake, which stands 20,310 feet above sea level and is North America's tallest peak. Formerly known as Mount McKinley, Denali attracts approximately 1,100 permitted climbers each season, though it's just as beautiful to admire from the ground.",
            IMAGES_FOLDER_ICONS + "landscapes/denali.jpg",
            "5"
        ),
        Item(
            "Halong Bay: Vietnam",
            "While many agree that Halong Bay's (also Ha Long Bay) more than 1,600 monsoon-eroded islands are beautiful and deserving of a place on UNESCO's World Heritage List, others warn that six million annual cruisers are ruining the environment and ambiance. The best way to view the area's upright karst formations is on a licensed three-day cruise. These excursions include time to explore the bay and some of its more remote islands before and after visitors from Hanoi arrive.",
            IMAGES_FOLDER_ICONS + "landscapes/halong.jpg",
            "6"
        ),
        Item(
            "Luberon Regional Nature Park: Ménerbes, France",
            "Though Provence features a variety of stunning landscapes, few are as breathtaking as the Luberon Regional Nature Park. Visit between the end of June and early August and you'll see (and smell) acres of blooming lavender plants. The herb's oil, which is known for its medicinal qualities, is pressed in local distilleries and workshops that welcome travelers, but for more insight about the plant, head to the Lavender Museum in Coustellet.",
            IMAGES_FOLDER_ICONS + "landscapes/luberon.jpg",
            "7"
        ),
        Item(
            "Laughing Waters: Jamaica",
            "This gold-hued shore east of Dunn's River Falls & Park is where Honey Ryder (Ursula Andress) famously rose from the waves to greet James Bond (Sean Connery) in \"Dr. No.\" Today, the beach is home to a private wedding venue with fern covered cliffs, small waterfalls and shallow surf, but it can be seen during select boat tours or by invitation from the St. Ann Development Company, its protector.",
            IMAGES_FOLDER_ICONS + "landscapes/laughing.jpg",
            "8"
        )
    )


    data class Item(val title: String, val desc: String, val image: String, val id: String)


}
