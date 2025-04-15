#!/usr/bin/env bash
# --------------------------------------------
# simple-interest.sh – Tính lãi đơn siêu tốc
# Usage: ./simple-interest.sh <principal> <rate%> <years>
# Example: ./simple-interest.sh 1000000 7.5 3
# --------------------------------------------

set -euo pipefail

if [[ $# -ne 3 ]]; then
  echo "Usage: $(basename "$0") <principal> <annual_rate_percent> <years>"
  exit 1
fi

P=$1  # principal
R=$2  # annual interest rate in percent
T=$3  # time in years

num_regex='^[0-9]+([.][0-9]+)?$'
for arg in "$P" "$R" "$T"; do
  if ! [[ $arg =~ $num_regex ]]; then
    echo "Error: '$arg' is not a valid number" >&2
    exit 2
  fi
done

# SI = P * R * T / 100
SI=$(bc -l <<< "scale=2; $P * $R * $T / 100")
AMOUNT=$(bc -l <<< "scale=2; $P + $SI")

printf "Principal (P): %s\nRate (R): %s%%\nTime (T): %s year(s)\n" "$P" "$R" "$T"
printf "Simple Interest (SI): %.2f\nTotal Amount (P+SI): %.2f\n" "$SI" "$AMOUNT"
