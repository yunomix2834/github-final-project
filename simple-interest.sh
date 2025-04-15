#!/usr/bin/env bash
# simple-interest.sh — Tính lãi đơn (Simple Interest)
# Công thức: SI = P * R * T / 100

set -euo pipefail

usage() {
  echo "Usage: $0 <principal> <annual_rate_%> <time_years>"
  echo "Example: $0 1000000 7.5 3"
  exit 1
}

[[ $# -ne 3 ]] && usage

P=$1   # Số tiền gốc
R=$2   # Lãi suất (%/năm)
T=$3   # Thời gian (năm)

# bc hỗ trợ số thực
SI=$(bc -l <<< "$P * $R * $T / 100")

printf "Số tiền gốc (P): %'d\n" "$P"
printf "Lãi suất (R):    %.2f%%/năm\n" "$R"
printf "Thời gian (T):   %.2f năm\n" "$T"
printf "=> Lãi đơn (SI): %'.2f\n" "$SI"
