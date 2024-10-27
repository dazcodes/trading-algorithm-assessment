import { useRef } from "react";
import "./PriceCell.css";

interface PriceCellProps {
  price: number;
}

export const PriceCell = ({ price }: PriceCellProps) => {
  // Using useRef to store the previous price for comparison
  const lastPriceRef = useRef(price);

  // Calculate the price difference
  const priceDiff = price - lastPriceRef.current;

  // Update the reference to the current price after calculating the difference
  lastPriceRef.current = price;

  // Determine the direction symbol
  let directionSymbol = "";
  if (priceDiff > 0) {
    directionSymbol = "↑"; // Price has increased
  } else if (priceDiff < 0) {
    directionSymbol = "↓"; // Price has decreased
  }

  return (
    <td className="price-cell">
      {price} <span className="price-arrow">{directionSymbol}</span>
    </td>
  );
};
