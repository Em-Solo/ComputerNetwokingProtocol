package RecipeExchange;

import java.util.HashMap;
import java.util.Map;

public class Recipes {

    private static final HashMap<String, String> idToRecipeNames = new HashMap<String, String>(){
        {
            put("0", "TruffleChickenAndPotatoGratin");
            put("1", "nextLevelPaella");
            put("2", "stringSalmonSpinachWithTartareCream");
            put("3", "honeyGlazedSpicedRoastGooseAndConfitPotatoes");
            put("4", "LambChopsWithChilliAndLigurianBroadBeanPesto");
        }
    };

    private static final String truffleChickenAndPotatoGratin = "Ingredients\n" +
            "2 chicken legs , skin on\n" +
            "1 tbsp salted butter , softened, plus a little extra, melted\n" +
            "1 small black truffle from a jar, sliced into very thin discs (use a mandoline if you have one) or a jar of truffle & porcini paste\n" +
            "125ml double cream\n" +
            "125ml chicken stock\n" +
            "1 garlic clove , peeled\n" +
            "1 bay leaf\n" +
            "6 sage leaves , plus a few extra for topping\n" +
            "500g floury potatoes , peeled and finely sliced (use a mandoline if you have one)\n" +
            "15g parmesan , finely grated\n" +
            "watercress salad , to serve\n" +
            "Method\n" +
            "STEP 1\n" +
            "Carefully pull the skin on the chicken legs away from the flesh, trying not to break the skin, then rub ¼ tbsp butter between the skin and flesh on each leg. Carefully put half of the truffle discs on top of the butter, or smear on some truffle paste and smooth back the skin. Season the skin, then set aside in the fridge.\n" +
            "\n" +
            "STEP 2\n" +
            "Heat the double cream and stock together with the garlic cloves, remaining truffle, bay and sage then, once simmering, set aside to infuse for 30 mins.\n" +
            "\n" +
            "STEP 3\n" +
            "Heat oven to 180C/160C fan/gas 4 and butter the inside of a shallow baking dish that will fit both chicken legs. Layer the potatoes, adding the truffle slices from the cream between the layers along with some seasoning, then strain over the infused cream and stock mixture. Sprinkle over the parseman and scatter over the remaining sage leaves. Place the chicken legs on top, brush with melted butter, then roast for 1 hr or until the potatoes are cooked and the chicken golden and cooked through. Allow to rest for 10 mins, letting the chicken juices drip into the potatoes, then serve with a watercress salad.";

    private static final String nextLevelPaella = "Ingredients\n" +
            "3 tbsp olive oil\n" +
            "10 large raw tiger prawns in their shells, heads removed and kept\n" +
            "small bunch of parsley, leaves and stalks separated, leaves roughly chopped\n" +
            "100ml dry sherry or white wine\n" +
            "500g mussels\n" +
            "large pinch of saffron strands\n" +
            "150g cooking chorizo, cut into chunks\n" +
            "1 onion, finely chopped\n" +
            "3 garlic cloves, finely chopped\n" +
            "1 medium squid (about 300g), cleaned and cut into rings with tentacles intact\n" +
            "2 ripe tomatoes, roughly chopped\n" +
            "250g paella rice\n" +
            "100g frozen podded broad beans or peas (or a mixture of the two), defrosted\n" +
            "1 lemon, finely zested then cut into wedges\n" +
            "smoked sea salt (optional)\n" +
            "Method\n" +
            "STEP 1\n" +
            "Heat 1 tbsp of the oil in a wide, shallow pan. Add the prawn heads and parsley stalks and sizzle until the heads turn pink, then mash with a potato masher. Pour over the sherry or wine and 300ml water, season with salt and simmer for 10 mins to make a stock, mashing the prawn heads as they cook.\n" +
            "\n" +
            "STEP 2\n" +
            "Scatter the mussels into the pan, cover the pan loosely with a lid or tea towel, then put over a high heat for 3-4 mins until the mussels just open. Stir to release the mussel juices, then pour the contents of the pan into a colander set over a large bowl containing the saffron. Let the saffron steep in the stock – you will need 700ml in total, so top up with water if needed and give everything a good stir. Pick the mussels out from the colander, then set aside.\n" +
            "\n" +
            "STEP 3\n" +
            "Wipe out the pan and add the rest of the olive oil. Sizzle the chorizo until it releases its oil, then add the onion and garlic and cook until softened. Add the squid and turn over until it turns white. Add the tomatoes and cook down for a minute, then pour over most of the stock, give everything a good stir and bring to the boil. Scatter the rice over the stock, stir well once, then boil vigorously for 5 mins. Reduce the heat to the lowest setting and slowly simmer for 10 mins without stirring until the rice has absorbed most of the liquid.\n" +
            "\n" +
            "STEP 4\n" +
            "Tuck the prawn tails into the rice and simmer for 5 mins, turning them over until cooked through. Stir through the mussels and broad beans or peas. Taste the rice – if it is still a little raw but the pan is dry, add a splash more stock and continue to cook; if it’s too soupy, then increase the heat to cook off the last of the stock.\n" +
            "\n" +
            "STEP 5\n" +
            "Once the rice is just cooked, turn off the heat and cover with a tea towel for a few minutes. Scatter over the parsley leaves and lemon zest, then season with smoked salt if you like. Stir everything once, then serve straight from the pan, with lemon wedges on the side.";


    private static final String stringSalmonSpinachWithTartareCream = "Ingredients\n" +
            "1 tsp sunflower or vegetable oil\n" +
            "2 skinless salmon fillets\n" +
            "250g bag spinach\n" +
            "2 tbsp reduced-fat crème fraîche\n" +
            "juice ½ lemon\n" +
            "1 tsp caper, drained\n" +
            "2 tbsp flat-leaf parsley, chopped\n" +
            "lemon wedges, to serve\n" +
            "Method\n" +
            "STEP 1\n" +
            "Heat the oil in a pan, season the salmon on both sides, then fry for 4 mins each side until golden and the flesh flakes easily. Leave to rest on a plate while you cook the spinach.\n" +
            "\n" +
            "STEP 2\n" +
            "Tip the leaves into the hot pan, season well, then cover and leave to wilt for 1 min, stirring once or twice. Spoon the spinach onto plates, then top with the salmon. Gently heat the crème fraîche in the pan with a squeeze of the lemon juice, the capers and parsley, then season to taste. Be careful not to let it boil. Spoon the sauce over the fish, then serve with lemon wedges.";

    private static final String honeyGlazedSpicedRoastGooseAndConfitPotatoes = "Ingredients\n" +
            "5kg oven-ready goose , trussed for roasting\n" +
            "3 small onions , halved\n" +
            "1 garlic bulb , halved, plus 2 cloves, finely chopped\n" +
            "large rosemary sprig\n" +
            "1 orange , halved\n" +
            "2 tbsp sunflower oil\n" +
            "1 ½kg Maris Piper potatoes , peeled and thickly sliced\n" +
            "small bunch parsley , finely chopped\n" +
            "For the glaze\n" +
            "3 tbsp good-quality honey (lavender honey works well)\n" +
            "¼ tsp cinnamon\n" +
            "¼ tsp ground cloves\n" +
            "Method\n" +
            "STEP 1\n" +
            "Heat oven to 200C/180C fan/gas 6. For the glaze, mix the honey with the spices and lots of cracked pepper, then set aside. Remove all the fat from inside the bird and, using the point of a small knife or skewer, prick the skin all over, paying particular attention to the areas under the wings and around the legs. Sit the goose in an empty sink and slowly pour over a kettleful of boiling water. Repeat the process a few more times (the more the better) until the skin is tight and glossy. Leave the goose to cool, then pat dry with kitchen paper. \n" +
            "\n" +
            "STEP 2\n" +
            "Season the inside of the goose and stuff with the onions, halved garlic bulb, rosemary and orange. Rub the goose all over with the oil and season generously with salt. Sit the goose on a wire rack in a large roasting tin and tightly cover with a large piece of foil. Cook in the oven for 1 hr 30 mins.\n" +
            "\n" +
            "STEP 3\n" +
            "Remove from the oven, discard the foil and use oven gloves to lift the rack (and the goose) out of the roasting tin. Pour all the fat from the tin into a bowl and set aside. Scatter the potato slices in the roasting tin, season with salt and mix with a small drizzle of the fat. Sit the goose back in the tin – on top of the potatoes – and cover with foil, then roast for 1 hr. Remove the foil, then brush the goose all over with the honey glaze and return to the oven for 15 mins. Transfer the goose to a large board or platter to rest, uncovered, in a warmish place for 30 mins.\n" +
            "\n" +
            "STEP 4\n" +
            "Carefully pour away most of the fat from the potatoes. If your roasting tin is flameproof, finish browning the potatoes on top of the stove; if not, place the potatoes back in the oven until crisp and golden brown. Stir the parsley and the chopped garlic through the potatoes just before serving. Carve the goose at the table and serve with the potatoes. ";

    private static final String lambChopsWithChilliAndLigurianBroadBeanPesto = "Ingredients\n" +
            "12 lamb cutlets, the bones well scraped so they’re neat (you can ask your butcher to do this for you)\n" +
            "3 tbsp olive oil\n" +
            "½ lemon , juiced, plus wedges to serve\n" +
            "6 tsp chilli flakes (use less if you prefer less heat)\n" +
            "For the pesto\n" +
            "400g broad beans (podded weight, about 1.4kg unpodded)\n" +
            "2 garlic cloves , chopped\n" +
            "8 mint sprigs, leaves picked and torn, plus extra to serve\n" +
            "8 anchovy fillets, chopped\n" +
            "½ small fennel bulb\n" +
            "120-150ml extra virgin olive oil , plus 2 tbsp and extra to serve\n" +
            "2 tbsp pecorino , grated\n" +
            "½ lemon , juiced or to taste\n" +
            "Method\n" +
            "STEP 1\n" +
            "Put the chops in a large bowl with the olive oil, lemon juice, chilli and some black pepper. Turn them with your hands, cover and put in the fridge to marinate for a few hours.\n" +
            "\n" +
            "STEP 2\n" +
            "Shell the broad beans and cook them in boiling water for 2-3 mins until tender. Drain and rinse in cold water. Slip each bean out of its skin, put them in the bowl of a food processor and add the garlic, mint and anchovy fillets. Remove any fronds from the fennel and set them aside with the tips. Take off any coarse outer layer, split the fennel in half and remove the hard core from each piece. Discard these. Chop the flesh and the reserved fennel trimmings. Heat 2 tbsp of the oil in a small frying pan and fry the fennel flesh gently for 10 mins until soft.\n" +
            "\n" +
            "STEP 3\n" +
            "Tip the fennel into the food processor along with the cheese and a little seasoning. Blitz to a purée while pouring in 120ml of the olive oil. Taste and add the lemon juice. You might find you want a bit more olive oil as well, or a little water to produce a thinner consistency. Remember to season again if you add water. Scrape the purée into a bowl and keep covered in the fridge until you’re ready to serve, but let it come to room temperature before you cook the chops.\n" +
            "\n" +
            "STEP 4\n" +
            "Take the chops out of the fridge and bring to room temperature. Heat a griddle pan over a high heat. Lift the chops out of the marinade and, when the pan is really hot, cook until well coloured on each side, seasoning as you go. Press the meaty bit down so that they get good griddle marks. The chops should be pink in the middle when you serve them – they only take 1½ mins on each side as they’re so small. Do this in batches if you need to.\n" +
            "\n" +
            "STEP 5\n" +
            "Serve the chops on top of the purée with some mint scattered over, any reserved fennel fonds, a drizzle of extra virgin olive oil and some lemon wedges on the side.";

    private static final HashMap<String, String> idToRecipeContent = new HashMap<String, String>(){
        {
            put("0", truffleChickenAndPotatoGratin);
            put("1", nextLevelPaella);
            put("2", stringSalmonSpinachWithTartareCream);
            put("3", honeyGlazedSpicedRoastGooseAndConfitPotatoes);
            put("4", lambChopsWithChilliAndLigurianBroadBeanPesto);
        }
    };

    public String listRepresentationOfSpecificRecipes (String recipeName) {

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : idToRecipeNames.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.toLowerCase().contains(recipeName.toLowerCase())) {
                sb = sb.append(key);
                sb = sb.append(":");
                sb = sb.append(value);
                sb = sb.append("\n");
            }

        }

        return sb.toString();
    }

    public String recipeFromId (String id) {
        return idToRecipeContent.get(id);
    }

}
