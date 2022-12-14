package me.sshcrack.fairylights.server.item.crafting;

import com.google.common.collect.ImmutableList;
import me.sshcrack.fairylights.FairyLightsMod;
import me.sshcrack.fairylights.server.item.DyeableItem;
import me.sshcrack.fairylights.server.item.FLItems;
import me.sshcrack.fairylights.server.item.HangingLightsConnectionItem;
import me.sshcrack.fairylights.server.string.StringTypes;
import me.sshcrack.fairylights.util.Blender;
import me.sshcrack.fairylights.util.OreDictUtils;
import me.sshcrack.fairylights.util.Utils;
import me.sshcrack.fairylights.util.crafting.GenericRecipe;
import me.sshcrack.fairylights.util.crafting.GenericRecipeBuilder;
import me.sshcrack.fairylights.util.crafting.OtherTags;
import me.sshcrack.fairylights.util.crafting.ingredient.*;
import me.sshcrack.fairylights.util.styledstring.StyledString;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class FLCraftingRecipes {
    private FLCraftingRecipes() {
    }

    public static <T extends CraftingRecipe> RecipeSerializer<T> register(String name, Supplier<RecipeSerializer<T>> serializerSupplier) {
        Identifier id = new Identifier(FairyLightsMod.ModID, name);
        return Registry.register(Registry.RECIPE_SERIALIZER, id, serializerSupplier.get());
    }

    public static final RecipeSerializer<GenericRecipe> HANGING_LIGHTS = register("crafting_special_hanging_lights", makeSerializer(FLCraftingRecipes::createHangingLights));

    public static final RecipeSerializer<GenericRecipe> HANGING_LIGHTS_AUGMENTATION = register("crafting_special_hanging_lights_augmentation", makeSerializer(FLCraftingRecipes::createHangingLightsAugmentation));

    public static final RecipeSerializer<GenericRecipe> TINSEL_GARLAND = register("crafting_special_tinsel_garland", makeSerializer(FLCraftingRecipes::createTinselGarland));

    public static final RecipeSerializer<GenericRecipe> PENNANT_BUNTING = register("crafting_special_pennant_bunting", makeSerializer(FLCraftingRecipes::createPennantBunting));

    public static final RecipeSerializer<GenericRecipe> PENNANT_BUNTING_AUGMENTATION = register("crafting_special_pennant_bunting_augmentation", makeSerializer(FLCraftingRecipes::createPennantBuntingAugmentation));

    public static final RecipeSerializer<GenericRecipe> TRIANGLE_PENNANT = register("crafting_special_triangle_pennant", makeSerializer(FLCraftingRecipes::createTrianglePennant));

    public static final RecipeSerializer<GenericRecipe> SPEARHEAD_PENNANT = register("crafting_special_spearhead_pennant", makeSerializer(FLCraftingRecipes::createSpearheadPennant));

    public static final RecipeSerializer<GenericRecipe> SWALLOWTAIL_PENNANT = register("crafting_special_swallowtail_pennant", makeSerializer(FLCraftingRecipes::createSwallowtailPennant));

    public static final RecipeSerializer<GenericRecipe> SQUARE_PENNANT = register("crafting_special_square_pennant", makeSerializer(FLCraftingRecipes::createSquarePennant));

    public static final RecipeSerializer<GenericRecipe> FAIRY_LIGHT = register("crafting_special_fairy_light", makeSerializer(FLCraftingRecipes::createFairyLight));

    public static final RecipeSerializer<GenericRecipe> PAPER_LANTERN = register("crafting_special_paper_lantern", makeSerializer(FLCraftingRecipes::createPaperLantern));

    public static final RecipeSerializer<GenericRecipe> ORB_LANTERN = register("crafting_special_orb_lantern", makeSerializer(FLCraftingRecipes::createOrbLantern));

    public static final RecipeSerializer<GenericRecipe> FLOWER_LIGHT = register("crafting_special_flower_light", makeSerializer(FLCraftingRecipes::createFlowerLight));

    public static final RecipeSerializer<GenericRecipe> CANDLE_LANTERN_LIGHT = register("crafting_special_candle_lantern_light", makeSerializer(FLCraftingRecipes::createCandleLanternLight));

    public static final RecipeSerializer<GenericRecipe> OIL_LANTERN_LIGHT = register("crafting_special_oil_lantern_light", makeSerializer(FLCraftingRecipes::createOilLanternLight));

    public static final RecipeSerializer<GenericRecipe> JACK_O_LANTERN = register("crafting_special_jack_o_lantern", makeSerializer(FLCraftingRecipes::createJackOLantern));

    public static final RecipeSerializer<GenericRecipe> SKULL_LIGHT = register("crafting_special_skull_light", makeSerializer(FLCraftingRecipes::createSkullLight));

    public static final RecipeSerializer<GenericRecipe> GHOST_LIGHT = register("crafting_special_ghost_light", makeSerializer(FLCraftingRecipes::createGhostLight));

    public static final RecipeSerializer<GenericRecipe> SPIDER_LIGHT = register("crafting_special_spider_light", makeSerializer(FLCraftingRecipes::createSpiderLight));

    public static final RecipeSerializer<GenericRecipe> WITCH_LIGHT = register("crafting_special_witch_light", makeSerializer(FLCraftingRecipes::createWitchLight));

    public static final RecipeSerializer<GenericRecipe> SNOWFLAKE_LIGHT = register("crafting_special_snowflake_light", makeSerializer(FLCraftingRecipes::createSnowflakeLight));

    public static final RecipeSerializer<GenericRecipe> HEART_LIGHT = register("crafting_special_heart_light", makeSerializer(FLCraftingRecipes::createHeartLight));

    public static final RecipeSerializer<GenericRecipe> MOON_LIGHT = register("crafting_special_moon_light", makeSerializer(FLCraftingRecipes::createMoonLight));

    public static final RecipeSerializer<GenericRecipe> STAR_LIGHT = register("crafting_special_star_light", makeSerializer(FLCraftingRecipes::createStarLight));

    public static final RecipeSerializer<GenericRecipe> ICICLE_LIGHTS = register("crafting_special_icicle_lights", makeSerializer(FLCraftingRecipes::createIcicleLights));

    public static final RecipeSerializer<GenericRecipe> METEOR_LIGHT = register("crafting_special_meteor_light", makeSerializer(FLCraftingRecipes::createMeteorLight));

    public static final RecipeSerializer<GenericRecipe> LIGHT_TWINKLE = register("crafting_special_light_twinkle", makeSerializer(FLCraftingRecipes::createLightTwinkle));

    public static final RecipeSerializer<GenericRecipe> COLOR_CHANGING_LIGHT = register("crafting_special_color_changing_light", makeSerializer(FLCraftingRecipes::createColorChangingLight));

    public static final RecipeSerializer<GenericRecipe> EDIT_COLOR = register("crafting_special_edit_color", makeSerializer(FLCraftingRecipes::createDyeColor));

    public static final RecipeSerializer<CopyColorRecipe> COPY_COLOR = register("crafting_special_copy_color", makeSerializer(CopyColorRecipe::new));

    public static final TagKey<Item> LIGHTS = TagKey.of(Registry.ITEM_KEY, new Identifier(FairyLightsMod.ModID, "lights"));

    public static final TagKey<Item> TWINKLING_LIGHTS = TagKey.of(Registry.ITEM_KEY, new Identifier(FairyLightsMod.ModID, "twinkling_lights"));

    public static final TagKey<Item> PENNANTS = TagKey.of(Registry.ITEM_KEY, new Identifier(FairyLightsMod.ModID, "pennants"));

    public static final TagKey<Item> DYEABLE = TagKey.of(Registry.ITEM_KEY, new Identifier(FairyLightsMod.ModID, "dyeable"));

    public static final TagKey<Item> DYEABLE_LIGHTS = TagKey.of(Registry.ITEM_KEY, new Identifier(FairyLightsMod.ModID, "dyeable_lights"));

    public static final RegularIngredient DYE_SUBTYPE_INGREDIENT = new BasicRegularIngredient(LazyTagIngredient.of(ConventionalItemTags.DYES)) {
        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            return DyeableItem.getDyeColor(output).map(dye -> ImmutableList.of(OreDictUtils.getDyes(dye))).orElse(ImmutableList.of());
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public void matched(final ItemStack ingredient, final NbtCompound nbt) {
            DyeableItem.setColor(nbt, OreDictUtils.getDyeColor(ingredient));
        }
    };

    private static <T extends CraftingRecipe> Supplier<RecipeSerializer<T>> makeSerializer(final Function<Identifier, T> factory) {
        return () -> new SpecialRecipeSerializer<>(factory);
    }

    private static GenericRecipe createDyeColor(final Identifier name) {
        return new GenericRecipeBuilder(name, () -> EDIT_COLOR)
                .withShape("I")
                .withIngredient('I', DYEABLE).withOutput('I')
                .withAuxiliaryIngredient(new BasicAuxiliaryIngredient<Blender>(LazyTagIngredient.of(ConventionalItemTags.DYES), true, 8) {
                    @Override
                    public Blender accumulator() {
                        return new Blender();
                    }

                    @Override
                    public void consume(final Blender data, final ItemStack ingredient) {
                        data.add(DyeableItem.getColor(OreDictUtils.getDyeColor(ingredient)));
                    }

                    @Override
                    public boolean finish(final Blender data, final NbtCompound nbt) {
                        DyeableItem.setColor(nbt, data.blend());
                        return false;
                    }
                })
                .build();
    }

    private static GenericRecipe createLightTwinkle(final Identifier name) {
        return new GenericRecipeBuilder(name, () -> LIGHT_TWINKLE)
                .withShape("L")
                .withIngredient('L', TWINKLING_LIGHTS).withOutput('L')
                .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(LazyTagIngredient.of(OtherTags.GLOWSTONE_DUSTS), true, 1) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return useInputsForTagBool(output, "twinkle", true) ? super.getInput(output) : ImmutableList.of();
                    }

                    @Override
                    public void present(final NbtCompound nbt) {
                        nbt.putBoolean("twinkle", true);
                    }

                    @Override
                    public void absent(final NbtCompound nbt) {
                        nbt.putBoolean("twinkle", false);
                    }

                    @Override
                    public void addTooltip(final List<Text> tooltip) {
                        super.addTooltip(tooltip);
                        tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.twinkling_lights.glowstone"));
                    }
                })
                .build();
    }

    private static GenericRecipe createColorChangingLight(final Identifier name) {
        return new GenericRecipeBuilder(name, () -> COLOR_CHANGING_LIGHT)
                .withShape("IG")
                .withIngredient('I', DYEABLE_LIGHTS).withOutput('I')
                .withIngredient('G', OtherTags.NUGGETS_GOLD)
                .withAuxiliaryIngredient(new BasicAuxiliaryIngredient<NbtList>(LazyTagIngredient.of(ConventionalItemTags.DYES), true, 8) {
                    @Override
                    public NbtList accumulator() {
                        return new NbtList();
                    }

                    @Override
                    public void consume(final NbtList data, final ItemStack ingredient) {
                        data.add(NbtInt.of(DyeableItem.getColor(OreDictUtils.getDyeColor(ingredient))));
                    }

                    @Override
                    public boolean finish(final NbtList data, final NbtCompound nbt) {
                        if (!data.isEmpty()) {
                            if (nbt.contains("color", NbtCompound.INT_TYPE)) {
                                data.add(0, NbtInt.of(nbt.getInt("color")));
                                nbt.remove("color");
                            }
                            nbt.put("colors", data);
                        }
                        return false;
                    }
                })
                .build();
    }

    private static GenericRecipe createHangingLights(final Identifier name) {
        return new GenericRecipeBuilder(name, () ->HANGING_LIGHTS, FLItems.HANGING_LIGHTS)
                .withShape("I-I")
                .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
                .withIngredient('-', OtherTags.STRING)
                .withAuxiliaryIngredient(new LightIngredient(true))
                .withAuxiliaryIngredient(new InertBasicAuxiliaryIngredient(LazyTagIngredient.of(ConventionalItemTags.WHITE_DYES), false, 1) {
                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        final NbtCompound tag = output.getNbt();
                        return tag != null && HangingLightsConnectionItem.getString(tag) == StringTypes.WHITE_STRING ? super.getInput(output) : ImmutableList.of();
                    }

                    @Override
                    public void present(final NbtCompound nbt) {
                        HangingLightsConnectionItem.setString(nbt, StringTypes.WHITE_STRING);
                    }

                    @Override
                    public void absent(final NbtCompound nbt) {
                        HangingLightsConnectionItem.setString(nbt, StringTypes.BLACK_STRING);
                    }

                    @Override
                    public void addTooltip(final List<Text> tooltip) {
                        super.addTooltip(tooltip);
                        tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.hangingLights.string"));
                    }
                })
                .build();
    }

    private static boolean useInputsForTagBool(final ItemStack output, final String key, final boolean value) {
        final NbtCompound compound = output.getNbt();
        return compound != null && compound.getBoolean(key) == value;
    }

    /*
     *  The JEI shown recipe is adding glowstone, eventually I should allow a recipe to provide a number of
     *  different recipe layouts the the input ingredients can be generated for so I could show applying a
     *  new light pattern as well.
     */
    private static GenericRecipe createHangingLightsAugmentation(final Identifier name) {
        return new GenericRecipeBuilder(name, () ->HANGING_LIGHTS_AUGMENTATION, FLItems.HANGING_LIGHTS)
                .withShape("F")
                .withIngredient('F', new BasicRegularIngredient(Ingredient.ofItems(FLItems.HANGING_LIGHTS)) {
                    @Override
                    public ImmutableList<ItemStack> getInputs() {
                        return Arrays.stream(this.ingredient.getMatchingStacks())
                                .map(ItemStack::copy)
                                .flatMap(stack -> {
                                    stack.setNbt(new NbtCompound());
                                    return makeHangingLightsExamples(stack).stream();
                                }).collect(ImmutableList.toImmutableList());
                    }

                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        final ItemStack stack = output.copy();
                        final NbtCompound compound = stack.getNbt();
                        if (compound == null) {
                            return ImmutableList.of();
                        }
                        stack.setCount(1);
                        return ImmutableList.of(ImmutableList.of(stack));
                    }

                    @Override
                    public void matched(final ItemStack ingredient, final NbtCompound nbt) {
                        final NbtCompound compound = ingredient.getNbt();
                        if (compound != null) {
                            nbt.copyFrom(compound);
                        }
                    }
                })
                .withAuxiliaryIngredient(new LightIngredient(true) {
                    @Override
                    public ImmutableList<ItemStack> getInputs() {
                        return ImmutableList.of();
                    }

                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        return ImmutableList.of();
                    }
                })
                .build();
    }

    private static ImmutableList<ItemStack> makeHangingLightsExamples(final ItemStack stack) {
        return ImmutableList.of(
                makeHangingLights(stack, DyeColor.CYAN, DyeColor.MAGENTA, DyeColor.CYAN, DyeColor.WHITE),
                makeHangingLights(stack, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.CYAN, DyeColor.LIGHT_BLUE),
                makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PINK, DyeColor.CYAN, DyeColor.GREEN),
                makeHangingLights(stack, DyeColor.LIGHT_GRAY, DyeColor.PURPLE, DyeColor.LIGHT_GRAY, DyeColor.GREEN),
                makeHangingLights(stack, DyeColor.CYAN, DyeColor.YELLOW, DyeColor.CYAN, DyeColor.PURPLE)
        );
    }

    public static ItemStack makeHangingLights(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        NbtCompound compound = stack.getNbt();
        final NbtList lights = new NbtList();
        for (final DyeColor color : colors) {
            lights.add(DyeableItem.setColor(new ItemStack(FLItems.FAIRY_LIGHT), color).writeNbt(new NbtCompound()));
        }
        if (compound == null) {
            compound = new NbtCompound();
            stack.setNbt(compound);
        }
        compound.put("pattern", lights);
        HangingLightsConnectionItem.setString(compound, StringTypes.BLACK_STRING);
        return stack;
    }

    private static GenericRecipe createTinselGarland(final Identifier name) {
        return new GenericRecipeBuilder(name, () ->TINSEL_GARLAND, FLItems.TINSEL)
                .withShape(" P ", "I-I", " D ")
                .withIngredient('P', Items.PAPER)
                .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
                .withIngredient('-', OtherTags.STRING)
                .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
                .build();
    }

    private static GenericRecipe createPennantBunting(final Identifier name) {
        return new GenericRecipeBuilder(name, () ->PENNANT_BUNTING, FLItems.PENNANT_BUNTING)
                .withShape("I-I")
                .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
                .withIngredient('-', OtherTags.STRING)
                .withAuxiliaryIngredient(new PennantIngredient())
                .build();
    }

    private static GenericRecipe createPennantBuntingAugmentation(final Identifier name) {
        return new GenericRecipeBuilder(name, () ->PENNANT_BUNTING_AUGMENTATION, FLItems.PENNANT_BUNTING)
                .withShape("B")
                .withIngredient('B', new BasicRegularIngredient(Ingredient.ofItems(FLItems.PENNANT_BUNTING)) {
                    @Override
                    public ImmutableList<ItemStack> getInputs() {
                        return Arrays.stream(this.ingredient.getMatchingStacks())
                                .map(ItemStack::copy)
                                .flatMap(stack -> {
                                    stack.setNbt(new NbtCompound());
                                    return makePennantExamples(stack).stream();
                                }).collect(ImmutableList.toImmutableList());
                    }

                    @Override
                    public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
                        final NbtCompound compound = output.getNbt();
                        if (compound == null) {
                            return ImmutableList.of();
                        }
                        return ImmutableList.of(makePennantExamples(output));
                    }

                    @Override
                    public void matched(final ItemStack ingredient, final NbtCompound nbt) {
                        final NbtCompound compound = ingredient.getNbt();
                        if (compound != null) {
                            nbt.copyFrom(compound);
                        }
                    }
                })
                .withAuxiliaryIngredient(new PennantIngredient())
                .build();
    }

    private static ImmutableList<ItemStack> makePennantExamples(final ItemStack stack) {
        return ImmutableList.of(
                makePennant(stack, DyeColor.BLUE, DyeColor.YELLOW, DyeColor.RED),
                makePennant(stack, DyeColor.PINK, DyeColor.LIGHT_BLUE),
                makePennant(stack, DyeColor.ORANGE, DyeColor.WHITE),
                makePennant(stack, DyeColor.LIME, DyeColor.YELLOW)
        );
    }

    public static ItemStack makePennant(final ItemStack base, final DyeColor... colors) {
        final ItemStack stack = base.copy();
        NbtCompound compound = stack.getNbt();
        final NbtList pennants = new NbtList();
        for (final DyeColor color : colors) {
            final ItemStack pennant = new ItemStack(FLItems.TRIANGLE_PENNANT);
            DyeableItem.setColor(pennant, color);
            pennants.add(pennant.writeNbt(new NbtCompound()));
        }
        if (compound == null) {
            compound = new NbtCompound();
            stack.setNbt(compound);
        }
        compound.put("pattern", pennants);
        compound.put("text", StyledString.serialize(new StyledString()));
        return stack;
    }

    private static GenericRecipe createPennant(final Identifier name, final RecipeSerializer<GenericRecipe> serializer, final Item item, final String pattern) {
        return new GenericRecipeBuilder(name, () -> serializer, item)
                .withShape("- -", "PDP", pattern)
                .withIngredient('P', Items.PAPER)
                .withIngredient('-', OtherTags.STRING)
                .withIngredient('D', DYE_SUBTYPE_INGREDIENT)
                .build();
    }

    private static GenericRecipe createTrianglePennant(final Identifier name) {
        return createPennant(name, TRIANGLE_PENNANT, FLItems.TRIANGLE_PENNANT, " P ");
    }

    private static GenericRecipe createSpearheadPennant(final Identifier name) {
        return createPennant(name, SPEARHEAD_PENNANT, FLItems.SPEARHEAD_PENNANT, " PP");
    }

    private static GenericRecipe createSwallowtailPennant(final Identifier name) {
        return createPennant(name, SWALLOWTAIL_PENNANT, FLItems.SWALLOWTAIL_PENNANT, "P P");
    }

    private static GenericRecipe createSquarePennant(final Identifier name) {
        return createPennant(name, SQUARE_PENNANT, FLItems.SQUARE_PENNANT, "PPP");
    }

    private static GenericRecipe createFairyLight(final Identifier name) {
        return createLight(name, FAIRY_LIGHT, FLItems.FAIRY_LIGHT, b -> b
                .withShape(" I ", "IDI", " G ")
                .withIngredient('G', Blocks.GLASS_PANE)
        );
    }

    private static GenericRecipe createPaperLantern(final Identifier name) {
        return createLight(name, PAPER_LANTERN, FLItems.PAPER_LANTERN, b -> b
                .withShape(" I ", "PDP", "PPP")
                .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createOrbLantern(final Identifier name) {
        return createLight(name, ORB_LANTERN, FLItems.ORB_LANTERN, b -> b
                .withShape(" I ", "SDS", " W ")
                .withIngredient('S', OtherTags.STRING)
                .withIngredient('W', Items.WHITE_WOOL)
        );
    }

    private static GenericRecipe createFlowerLight(final Identifier name) {
        return createLight(name, FLOWER_LIGHT, FLItems.FLOWER_LIGHT, b -> b
                .withShape(" I ", "RDB", " Y ")
                .withIngredient('R', Items.POPPY)
                .withIngredient('Y', Items.DANDELION)
                .withIngredient('B', Items.BLUE_ORCHID)
        );
    }

    private static GenericRecipe createCandleLanternLight(final Identifier name) {
        return createLight(name, CANDLE_LANTERN_LIGHT, FLItems.CANDLE_LANTERN_LIGHT, b -> b
                .withShape(" I ", "GDG", "IGI")
                .withIngredient('G', OtherTags.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createOilLanternLight(final Identifier name) {
        return createLight(name, OIL_LANTERN_LIGHT, FLItems.OIL_LANTERN_LIGHT, b -> b
                .withShape(" I ", "SDS", "IGI")
                .withIngredient('S', Items.STICK)
                .withIngredient('G', Blocks.GLASS_PANE)
        );
    }

    private static GenericRecipe createJackOLantern(final Identifier name) {
        return createLight(name, JACK_O_LANTERN, FLItems.JACK_O_LANTERN, b -> b
                .withShape(" I ", "SDS", "GPG")
                .withIngredient('S', ItemTags.WOODEN_SLABS)
                .withIngredient('G', Items.TORCH)
                .withIngredient('P', Items.JACK_O_LANTERN)
        );
    }

    private static GenericRecipe createSkullLight(final Identifier name) {
        return createLight(name, SKULL_LIGHT, FLItems.SKULL_LIGHT, b -> b
                .withShape(" I ", "IDI", " B ")
                .withIngredient('B', OtherTags.BONES)
        );
    }

    private static GenericRecipe createGhostLight(final Identifier name) {
        return createLight(name, GHOST_LIGHT, FLItems.GHOST_LIGHT, b -> b
                .withShape(" I ", "PDP", "IGI")
                .withIngredient('P', Items.PAPER)
                .withIngredient('G', Blocks.WHITE_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createSpiderLight(final Identifier name) {
        return createLight(name, SPIDER_LIGHT, FLItems.SPIDER_LIGHT, b -> b
                .withShape(" I ", "WDW", "SES")
                .withIngredient('W', Items.COBWEB)
                .withIngredient('S', OtherTags.STRING)
                .withIngredient('E', Items.SPIDER_EYE)
        );
    }

    private static GenericRecipe createWitchLight(final Identifier name) {
        return createLight(name, WITCH_LIGHT, FLItems.WITCH_LIGHT, b -> b
                .withShape(" I ", "BDW", " S ")
                .withIngredient('B', Items.GLASS_BOTTLE)
                .withIngredient('W', Items.WHEAT)
                .withIngredient('S', Items.STICK)
        );
    }

    private static GenericRecipe createSnowflakeLight(final Identifier name) {
        return createLight(name, SNOWFLAKE_LIGHT, FLItems.SNOWFLAKE_LIGHT, b -> b
                .withShape(" I ", "SDS", " G ")
                .withIngredient('S', Items.SNOWBALL)
                .withIngredient('G', Blocks.WHITE_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createHeartLight(final Identifier name) {
        return createLight(name, HEART_LIGHT, FLItems.HEART_LIGHT, b -> b
                .withShape(" I ", "IDI", " G ")
                .withIngredient('G', Blocks.RED_STAINED_GLASS_PANE)
        );
    }

    private static GenericRecipe createMoonLight(final Identifier name) {
        return createLight(name, MOON_LIGHT, FLItems.MOON_LIGHT, b -> b
                .withShape(" I ", "GDG", " C ")
                .withIngredient('G', Blocks.WHITE_STAINED_GLASS_PANE)
                .withIngredient('C', Items.CLOCK)
        );
    }


    private static GenericRecipe createStarLight(final Identifier name) {
        return createLight(name, STAR_LIGHT, FLItems.STAR_LIGHT, b -> b
                .withShape(" I ", "PDP", " G ")
                .withIngredient('P', Blocks.WHITE_STAINED_GLASS_PANE)
                .withIngredient('G', OtherTags.NUGGETS_GOLD)
        );
    }

    private static GenericRecipe createIcicleLights(final Identifier name) {
        return createLight(name, ICICLE_LIGHTS, FLItems.ICICLE_LIGHTS, b -> b
                .withShape(" I ", "GDG", " B ")
                .withIngredient('G', Blocks.WHITE_STAINED_GLASS_PANE)
                .withIngredient('B', Items.WATER_BUCKET)
        );
    }

    private static GenericRecipe createMeteorLight(final Identifier name) {
        return createLight(name, METEOR_LIGHT, FLItems.METEOR_LIGHT, b -> b
                .withShape(" I ", "GDG", "IPI")
                .withIngredient('G', OtherTags.GLOWSTONE_DUSTS)
                .withIngredient('P', Items.PAPER)
        );
    }

    private static GenericRecipe createLight(final Identifier name, final RecipeSerializer<GenericRecipe> serializer, final Item variant, final UnaryOperator<GenericRecipeBuilder> recipe) {
        return recipe.apply(new GenericRecipeBuilder(name, () -> serializer))
                .withIngredient('I', ConventionalItemTags.IRON_INGOTS)
                .withIngredient('D', FLCraftingRecipes.DYE_SUBTYPE_INGREDIENT)
                .withOutput(variant, 4)
                .build();
    }

    private static class LightIngredient extends BasicAuxiliaryIngredient<NbtList> {
        private LightIngredient(final boolean isRequired) {
            super(LazyTagIngredient.of(LIGHTS), isRequired, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final NbtCompound compound = output.getNbt();
            if (compound == null) {
                return ImmutableList.of();
            }
            final NbtList pattern = compound.getList("pattern", NbtCompound.COMPOUND_TYPE);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> lights = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                lights.add(ImmutableList.of(ItemStack.fromNbt(pattern.getCompound(i))));
            }
            return lights.build();
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public NbtList accumulator() {
            return new NbtList();
        }

        @Override
        public void consume(final NbtList patternList, final ItemStack ingredient) {
            patternList.add(ingredient.writeNbt(new NbtCompound()));
        }

        @Override
        public boolean finish(final NbtList pattern, final NbtCompound nbt) {
            if (pattern.size() > 0) {
                nbt.put("pattern", pattern);
            }
            return false;
        }

        @Override
        public void addTooltip(final List<Text> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.hangingLights.light"));
        }
    }

    private static class PennantIngredient extends BasicAuxiliaryIngredient<NbtList> {
        private PennantIngredient() {
            super(LazyTagIngredient.of(PENNANTS), true, 8);
        }

        @Override
        public ImmutableList<ImmutableList<ItemStack>> getInput(final ItemStack output) {
            final NbtCompound compound = output.getNbt();
            if (compound == null) {
                return ImmutableList.of();
            }
            final NbtList pattern = compound.getList("pattern", NbtCompound.COMPOUND_TYPE);
            if (pattern.isEmpty()) {
                return ImmutableList.of();
            }
            final ImmutableList.Builder<ImmutableList<ItemStack>> pennants = ImmutableList.builder();
            for (int i = 0; i < pattern.size(); i++) {
                pennants.add(ImmutableList.of(ItemStack.fromNbt(pattern.getCompound(i))));
            }
            return pennants.build();
        }

        @Override
        public boolean dictatesOutputType() {
            return true;
        }

        @Override
        public NbtList accumulator() {
            return new NbtList();
        }

        @Override
        public void consume(final NbtList patternList, final ItemStack ingredient) {
            patternList.add(ingredient.writeNbt(new NbtCompound()));
        }

        @Override
        public boolean finish(final NbtList pattern, final NbtCompound nbt) {
            if (pattern.size() > 0) {
                nbt.put("pattern", pattern);
                nbt.put("text", StyledString.serialize(new StyledString()));
            }
            return false;
        }

        @Override
        public void addTooltip(final List<Text> tooltip) {
            tooltip.add(Utils.formatRecipeTooltip("recipe.fairylights.pennantBunting.pennant"));
        }
    }
}
