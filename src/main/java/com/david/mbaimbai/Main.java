package com.david.mbaimbai;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

class ScratchGame {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ScratchGame \"C:\\projects\\cyberspeed\\src\\main\\resources\\config.json\" 100");
            return;
        }

        String configFilePath = args[0];
        int bettingAmount = Integer.parseInt(args[1]);
        Configuration configuration = parseConfiguration(configFilePath);

        if (configuration != null) {
            Probability probability = configuration.getProbabilities();
            Map<String, Symbol> symbols = configuration.getSymbols();
            Map<String, WinCombination> winCombinations = configuration.getWinCombinations();
            List<List<String>> matrix = generateMatrix(configuration.getRows(), configuration.getColumns(), probability);
            int reward = calculateRewards(matrix, probability, symbols, winCombinations);
            applyBonusSymbols(matrix, probability, symbols);
            outputResults(matrix, reward, symbols, winCombinations);
        }
    }

    static Configuration parseConfiguration(String configFilePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(configFilePath), Configuration.class);
        } catch (IOException e) {
            System.out.println("Error reading the configuration file: " + e.getMessage());
            return null;
        }
    }

    static List<List<String>> generateMatrix(int rows, int columns, Probability probabilities) {
        List<List<String>> matrix = new ArrayList<>();
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Invalid number of rows or columns.");
        }
        Map<String, Integer> standardSymbolProbabilities = probabilities.getStandardSymbols();
        if (standardSymbolProbabilities.isEmpty()) {
            throw new IllegalArgumentException("No standard symbols defined in probabilities.");
        }
        List<String> standardSymbols = new ArrayList<>(standardSymbolProbabilities.keySet());
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                String symbol = selectSymbolWithProbability(standardSymbolProbabilities, standardSymbols, random);
                row.add(symbol);
            }
            matrix.add(row);
        }

        return matrix;
    }

    private static String selectSymbolWithProbability(Map<String, Integer> probabilities, List<String> symbols, Random random) {
        int totalProbability = 0;
        for (String symbol : symbols) {
            totalProbability += probabilities.get(symbol);
        }

        int randomValue = random.nextInt(totalProbability);

        for (String symbol : symbols) {
            int symbolProbability = probabilities.get(symbol);
            if (randomValue < symbolProbability) {
                return symbol;
            }
            randomValue -= symbolProbability;
        }
        return symbols.get(0);
    }

    static int calculateRewards(List<List<String>> matrix, Probability probabilities, Map<String, Symbol> symbols, Map<String, WinCombination> winCombinations) {
        int totalReward = 0;
        for (List<String> row : matrix) {
            for (String cell : row) {
                if (symbols.containsKey(cell)) {
                    int symbolReward = symbols.get(cell).getRewardMultiplier();
                    for (WinCombination winCombination : winCombinations.values()) {
                        if (checkWinCombination(matrix, cell, winCombination)) {
                            symbolReward *= winCombination.getRewardMultiplier();
                        }
                    }
                    totalReward += symbolReward;
                }
            }
        }

        return totalReward;
    }

    private static boolean checkWinCombination(List<List<String>> matrix, String symbol, WinCombination winCombination) {
        int rowCount = matrix.size();
        int columnCount = matrix.get(0).size();
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (matrix.get(row).get(column).equals(symbol)) {
                    if (checkWinCombinationCriteria(matrix, symbol, winCombination, row, column)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean checkWinCombinationCriteria(List<List<String>> matrix, String symbol, WinCombination winCombination, int startRow, int startColumn) {
        int rowCount = matrix.size();
        int columnCount = matrix.get(0).size();
        if (winCombination.getWhen().equals("same_symbols")) {
            int count = 0;
            for (int column = startColumn; column < columnCount; column++) {
                if (matrix.get(startRow).get(column).equals(symbol)) {
                    count++;
                    if (count == winCombination.getCount()) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        if (winCombination.getWhen().equals("linear_symbols") && winCombination.getGroup().equals("vertically_linear_symbols")) {
            int count = 0;
            for (int row = startRow; row < rowCount; row++) {
                if (matrix.get(row).get(startColumn).equals(symbol)) {
                    count++;
                    if (count == winCombination.getCount()) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        if (winCombination.getWhen().equals("linear_symbols") && winCombination.getGroup().equals("ltr_diagonally_linear_symbols")) {
            int count = 0;
            for (int row = startRow, column = startColumn; row < rowCount && column < columnCount; row++, column++) {
                if (matrix.get(row).get(column).equals(symbol)) {
                    count++;
                    if (count == winCombination.getCount()) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        if (winCombination.getWhen().equals("linear_symbols") && winCombination.getGroup().equals("rtl_diagonally_linear_symbols")) {
            int count = 0;
            for (int row = startRow, column = startColumn; row < rowCount && column >= 0; row++, column--) {
                if (matrix.get(row).get(column).equals(symbol)) {
                    count++;
                    if (count == winCombination.getCount()) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    private static void applyBonusSymbols(List<List<String>> matrix, Probability probabilities, Map<String, Symbol> symbols) {
        boolean hasWinningCombinations = checkForWinningCombinations(matrix, probabilities);

        if (hasWinningCombinations) {
            Map<String, Integer> bonusSymbolProbabilities = probabilities.getBonusSymbols();
            String selectedBonusSymbol = selectRandomBonusSymbol(bonusSymbolProbabilities);

            if (selectedBonusSymbol != null) {
                applyBonusSymbolEffects(selectedBonusSymbol, symbols, matrix);
            }
        }
    }

    private static boolean checkForWinningCombinations(List<List<String>> matrix, Probability probabilities) {
        Map<String, WinCombination> winCombinations = probabilities.getWinCombinations();
        for (Map.Entry<String, WinCombination> entry : winCombinations.entrySet()) {
            String symbol = entry.getKey();
            WinCombination winCombination = entry.getValue();
            for (int row = 0; row < matrix.size(); row++) {
                for (int col = 0; col < matrix.get(0).size(); col++) {
                    if (matrix.get(row).get(col).equals(symbol)) {
                        if (checkWinCombination(matrix, symbol, winCombination)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    static String selectRandomBonusSymbol(Map<String, Integer> bonusSymbolProbabilities) {
        int totalWeight = bonusSymbolProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = (int) (Math.random() * totalWeight) + 1;
        for (Map.Entry<String, Integer> entry : bonusSymbolProbabilities.entrySet()) {
            String symbol = entry.getKey();
            int probability = entry.getValue();
            if (randomValue <= probability) {
                return symbol;
            }
            randomValue -= probability;
        }

        return null;
    }


    static void applyBonusSymbolEffects(String selectedBonusSymbol, Map<String, Symbol> symbols, List<List<String>> matrix) {
        Symbol bonusSymbol = symbols.get(selectedBonusSymbol);

        if (bonusSymbol != null) {
            String impact = bonusSymbol.getImpact();
            switch (impact) {
                case "multiply_reward":
                    multiplyRewards(matrix, symbols, bonusSymbol.getRewardMultiplier());
                    break;
                case "extra_bonus":
                    addExtraBonus(matrix, symbols, bonusSymbol.getExtra());
                    break;
                default:
                    break;
            }
        }
    }

    private static void multiplyRewards(List<List<String>> matrix, Map<String, Symbol> symbols, int multiplier) {
        for (int row = 0; row < matrix.size(); row++) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                String symbolName = matrix.get(row).get(col);
                Symbol symbol = symbols.get(symbolName);
                if (symbol != null && "standard".equals(symbol.getType())) {
                    int originalReward = symbol.getRewardMultiplier();
                    int newReward = originalReward * multiplier;
                    matrix.get(row).set(col, Integer.toString(newReward));
                }
            }
        }
    }


    private static void addExtraBonus(List<List<String>> matrix, Map<String, Symbol> symbols, int extra) {
        for (int row = 0; row < matrix.size(); row++) {
            for (int col = 0; col < matrix.get(0).size(); col++) {
                String symbolName = matrix.get(row).get(col);
                Symbol symbol = symbols.get(symbolName);
                if (symbol != null && "standard".equals(symbol.getType())) {
                    int originalReward = symbol.getRewardMultiplier();
                    int newReward = originalReward + extra;
                    matrix.get(row).set(col, Integer.toString(newReward));
                }
            }
        }
    }
    private static void outputResults(List<List<String>> matrix, int reward, Map<String, Symbol> symbols, Map<String, WinCombination> winCombinations) {
        Map<String, Object> result = new HashMap<>();
        result.put("matrix", matrix);
        result.put("reward", reward);
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        for (String symbolName : symbols.keySet()) {
            List<String> appliedCombinations = new ArrayList<>();
            for (WinCombination winCombination : winCombinations.values()) {
                if (winCombination.getGroup().equals(symbolName) && matrixMatchesCombination(matrix, symbolName, winCombination)) {
                    appliedCombinations.add(winCombination.getName());
                }
            }
            if (!appliedCombinations.isEmpty()) {
                appliedWinningCombinations.put(symbolName, appliedCombinations);
            }
        }
        result.put("applied_winning_combinations", appliedWinningCombinations);
        String appliedBonusSymbol = null;
        for (String symbolName : symbols.keySet()) {
            Symbol symbol = symbols.get(symbolName);
            if ("bonus".equals(symbol.getType()) && matrixContainsSymbol(matrix, symbolName)) {
                appliedBonusSymbol = symbolName;
                break;
            }
        }
        result.put("applied_bonus_symbol", appliedBonusSymbol);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writeValueAsString(result);
            System.out.println(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static boolean matrixMatchesCombination(List<List<String>> matrix, String symbol, WinCombination winCombination) {
        if ("same_symbols".equals(winCombination.getWhen())) {
            // Check if the symbol matches the win combination count
            int count = 0;
            for (List<String> row : matrix) {
                for (String cell : row) {
                    if (symbol.equals(cell)) {
                        count++;
                        if (count >= winCombination.getCount()) {
                            return true;
                        }
                    }
                }
            }
        } else if ("linear_symbols".equals(winCombination.getWhen())) {
            for (List<String> coveredArea : winCombination.getCoveredAreas()) {
                boolean matched = true;
                for (String cell : coveredArea) {
                    String[] coordinates = cell.split(":");
                    int row = Integer.parseInt(coordinates[0]);
                    int column = Integer.parseInt(coordinates[1]);
                    if (!symbol.equals(matrix.get(row).get(column))) {
                        matched = false;
                        break;
                    }
                }
                if (matched) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean matrixContainsSymbol(List<List<String>> matrix, String symbol) {
        for (List<String> row : matrix) {
            for (String cell : row) {
                if (symbol.equals(cell)) {
                    return true;
                }
            }
        }
        return false;
    }

}