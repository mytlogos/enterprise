package scrape.sources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class TestSources {
    public static List<String> testLinks() {
        String link = "https://www.wuxiaworld.com/novel/a-will-eternal/awe-chapter-528\n" +
                "http://eccentrictranslations.com/atf-chapter-149/\n" +
                "https://hikkinomori.mistbinder.org/2017/12/17/filwtv-74th-update-announcement/\n" +
                "https://www.asianhobbyist.com/akuyaku-tensei-dakedo-doushite-kou-natta-chapter-212/\n" +
                "https://www.webnovel.com/rssbook/8534263705001005/26875074501988919\n" +
                "http://www.sousetsuka.com/2018/04/death-march-kara-hajimaru-isekai.html\n" +
                "http://moonbunnycafe.com/careless-demon-volume-6-chapter-11-release/\n" +
                "https://yukkuri-literature-service.blogspot.de/2018/04/Genou04-05b.html\n" +
                "http://volarenovels.com/hssb-chapter-819/\n" +
                "https://kobatochan.com/i-am-the-monarch-chapter-209/\n" +
                "http://pyontranslations.com/i-never-run-out-of-mana-chapter-77/\n" +
                "http://liberspark.com/read/immortal/book-2-chapter-39\n" +
                "https://kungfubears.wordpress.com/2018/01/11/58-a-bad-premonition-that-cant-be-dealt-with/\n" +
                "https://fantasy-books.live/kusuriya-no-hitorigoto/kusuriya-no-hitorigoto-volume-4-chapter-12-calico-cat/\n" +
                "http://www.sciencemag.org/news/2018/04/swarm-black-holes-may-be-lurking-our-galaxy-s-heart?rss=1\n" +
                "https://www.oppatranslations.com/2018/02/15/main-character-hides-his-strength-chapter-127/\n" +
                "https://www.lightnovelbastion.com/release.php?p=1152\n" +
                "https://voidtranslations.wordpress.com/2017/04/21/my-daoist-life-chapter-58/\n" +
                "https://defiring.wordpress.com/2018/03/22/my-death-flags-show-no-sign-of-ending-chapter-99/\n" +
                "http://scrya.org/2018/04/02/disciple-releases-chapter-321-322/\n" +
                "https://sodtl.blogspot.de/2017/12/okih-57.html\n" +
                "http://gravitytales.com/post/reincarnator/reincarnator-chapter-345-348\n" +
                "https://ensjtrans.com/2017/03/31/rath-chapter-8-i-came-to-end-it-all-3/\n" +
//                "https://www.oyasumireads.com/2017/10/07/strangers-handbook-chapter-146/\n" +
                "http://www.rebirth.online/2018/04/05/tgfnsyl-614\n" +
                "https://shintranslations.com/2018/03/the-new-gate-vol-10-chapter-1-part-2/\n" +
                "https://www.baka-tsuki.org/project/index.php?title=Toaru_Majutsu_no_Index:NT_Volume15_Afterword\n" +
                "https://oniichanyamete.moe/2015/01/21/burikko-chapter-1/\n" +
                "http://www.wuxiaheroes.com/conquest/chapter-0/\n" +
                "http://www.spcnet.tv/forums/showthread.php/38694-%C3%A5%C2%A4%C2%A7%C3%A5%E2%80%9D%C2%90%C3%A5%C2%8F%C5%92%C3%A9%C2%BE%E2%84%A2%C3%A4%C2%BC%C2%A0-Da-Tang-Shuang-Long-Zhuan-by-Huang-Yi?s=fa7b6be6f9958ea84011409572ae87b7&p=1091981&viewfull=1#post1091981\n" +
                "https://gilatranslationmonster.wordpress.com/2015/03/18/dawn-traveler-chapter-1/\n" +
                "https://kudarajin.wordpress.com/2016/07/03/yukiguni-karigurashi-1/\n" +
                "https://paichuntranslations.com/2016/11/09/hon-issatsu-de-kototariru-isekai-ruro-monogatari-volume-1-illustrations/\n" +
                "https://starrydawntranslations.wordpress.com/katahane01/\n" +
                "https://cgtranslations.me/2017/12/30/konosuba-volume-1-prologue/\n" +
                "http://saigotranslation.com/wradk-index/wradk-chapter-2/\n" +
                "https://hajiko.wordpress.com/2015/02/11/ryuugoroshi-no-sugosu-hibi-v1-chapter-1-sudden-human-tropedo/\n" +
                "https://binhjamin.wordpress.com/sayonara-ryuusei-konnichiwa-jinsei/volume-1/illustrations/\n" +
                "https://unlimitednovelfailures.mangamatters.com/teaser/shinanai-otoko-ni-koi-shita-shoujo/shinanai-otoko-volume-01/#chapter01\n" +
                "https://kakkokaritranslations.com/2016/01/23/se-prologue/\n" +
                "https://wuxianation.com/the-dark-king/dk-ch-4.html\n" +
                "https://avertranslation.blogspot.de/p/shinigami-wo-tabeta-shoujo-volume-1.html\n" +
                "https://psychobarcodetranslations.wordpress.com/flamering/chapter-1/\n" +
                "http://myoniyonitranslations.com/the-king-of-the-battlefield/prologue/\n" +
                "http://jigglypuffsdiary.com/the-man-picked-up-by-the-gods/the-man-picked-up-by-the-gods-prologue-1/\n" +
                "https://youshokutranslations.wordpress.com/2015/08/09/the-other-world-dining-hall-prologue-chapter-1/\n" +
                "https://lesyt.xyz/novel/tsa/1\n" +
                "https://sunshowerfields.wordpress.com/2015/08/17/the-tang-dynastys-female-forensic-doctor-chapter-1/?-chapter-1/\n" +
                "https://lylisasmodeus.wordpress.com/2015/09/15/the-unicorn-legion-the-golden-capitol-prologue/\n" +
                "https://nightbreezetranslations.wordpress.com/important-information/\n" +
                "https://confusedtls.wordpress.com/2017/02/24/youkoso-jitsuryoku-teaser/\n" +
                "http://www.suiminchuudoku.net/tl/index.php?title=zaregoto:vol3:1\n" +
                "http://www.suiminchuudoku.net/tl/index.php?title=zaregoto:vol3:1\n" +
                "https://zirusmusings.com/2015/forsaken-hero-story-4-15/\n" +
                "https://honyakusite.wordpress.com/2017/02/20/ovrmmo-091-and-on-the-day-of-the-transaction/\n" +
                "https://durasama.wordpress.com/2016/01/10/arifureta-chapter-96/\n" +
                "http://moonbunnycafe.com/boku-wa-isekai-de-fuyo-mahou-to-shoukan-mahou-wo-tenbin-ni-kakeru/bifmsmtk-volume-2-chapter-42/\n" +
                "https://shalvationtranslations.wordpress.com/2016/08/29/dungeon-defense-volume-3-prologue/\n" +
                "https://lightnovelstranslations.com/\n" +
                "http://raisingthedead.ninja/2016/01/20/when-i-returned-home-what-i-found-was-fantasy-2-06/\n" +
                "https://konobuta.com/2016/11/16/uchimusume-chapter-57/\n" +
                "http://tseirptranslations.com/2016/06/is-b6c90.html\n" +
                "https://arsl31.wordpress.com/konjiki-no-wordmaster-chapter-210/\n" +
                "http://japtem.com/mg-volume-3-chapter-45/\n" +
                "https://maoyuuthetranslation.wordpress.com/volume-2/403-2/\n" +
                "https://izra709.wordpress.com/2016/05/09/monohito-chapter-36-first-lesson/\n" +
                "https://subudai11.wordpress.com/2016/04/08/chapter-106-bodyguard-pk-rich-girl/\n" +
                "https://mayonaizeshrimp.wordpress.com/2017/07/27/no-fatigue-72-solitary-pipe/\n" +
                "https://larvyde.wordpress.com/2016/10/07/orejimi-55/\n" +
                "http://www.novelsaga.com/otherworldly-evil-monarch-chapter-215/\n" +
                "https://manga0205.wordpress.com/2016/10/25/sendai-yuusha-wa-inkyou-shitai-chapter-126/\n" +
                "https://yoraikun.wordpress.com/2016/07/16/sevens-188/\n" +
                "http://infinitenoveltranslations.net/summoned-slaughterer-chapter-64-desecration-smile/\n" +
                "https://lionmaskrpt.wordpress.com/2016/10/01/184-the-faceoff/\n" +
                "https://shikkakutranslations.org/2016/05/30/kamigoroshi-no-eiyuu-to-nanatsu-no-seiyaku-chapter-40/\n" +
                "https://arkmachinetranslations.wordpress.com/chapter-9-the-return-of-the-king/\n" +
                "https://tensaitranslations.wordpress.com/2015/12/31/tsuyokute-new-saga-volume-2-ch09/\n" +
                "https://wcctranslation.wordpress.com/2016/03/16/chapter-79-valerie-of-the-flame-whip/\n" +
                "https://bayabuscotranslation.com/2017/02/16/world-teacher-69-cleaned-the-underworld-society-studies-for-a-silver-wolfkin/\n";
        return new ArrayList<>(Arrays.asList(link.split("\n")));
    }
}
