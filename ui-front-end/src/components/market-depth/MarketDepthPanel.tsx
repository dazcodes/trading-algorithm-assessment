interface MarketDepthPanelProps {
  data: MarketDepthRow[];
}

import "./MarketDepthPanel.css";
import { PriceCell } from "./PriceCell";
import { MarketDepthRow } from "./useMarketDepthData";

export const MarketDepthPanel = (props: MarketDepthPanelProps) => {
  const { data } = props;
  // Assuming a maximum quantity for width calculation (this can be dynamic)

  return (
    <table className="MarketDepthPanel">
      <div className="MarketDepthContainer">
        <thead>
          <tr>
            <th colSpan={2}>Bid</th>
            <th colSpan={2}></th>
            <th colSpan={2}>Ask</th>
          </tr>
          <tr>
            <th></th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Price</th>
            <th>Quantity</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr key={index}>
              {/* Display the level */}
              <td>{row.level}</td>

              {/* Bid Quantity with dynamic red bar */}
              <td className="bid">
                <div className="quantity-bar">
                  <div
                    className="quantity-fill bid-fill"
                    style={{
                      width: `${Math.min(row.bidQuantity, 100)}%`,
                    }}
                  >
                    <b>
                      <span className="quantity-value">{row.bidQuantity}</span>
                    </b>
                  </div>
                </div>
              </td>

              {/* Bid Price */}
              <b>
                {" "}
                <PriceCell price={row.bid} />
              </b>

              {/* Ask Price */}
              <b>
                <PriceCell price={row.offer} />
              </b>

              {/* Ask Quantity with dynamic blue bar */}
              <td className="ask">
                <div className="quantity-bar">
                  <div
                    className="quantity-fill ask-fill"
                    style={{
                      width: `${Math.min(row.offerQuantity, 100)}%`,
                    }}
                  >
                    <b>
                      <span className="quantity-value">
                        {row.offerQuantity}
                      </span>
                    </b>
                  </div>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </div>
    </table>
  );
};
