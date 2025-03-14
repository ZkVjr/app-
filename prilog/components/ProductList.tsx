import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';

interface Product {
  id: string;
  name: string;
  calories: number;
  quantity: number;
  price: number;
}

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [checkedProducts, setCheckedProducts] = useState<Record<string, boolean>>({});
  const [totalCalories, setTotalCalories] = useState<number>(0);
  const [totalPrice, setTotalPrice] = useState<number>(0);
  const { t, i18n } = useTranslation();

  // Обработка чекбокса
  const handleCheckboxChange = (productId: string) => {
    const product = products.find(p => p.id === productId);
    if (!product) return;

    const newCheckedState = !checkedProducts[productId];
    setCheckedProducts(prev => ({
      ...prev,
      [productId]: newCheckedState
    }));

    const caloriesChange = product.calories * (product.quantity || 1);
    const priceChange = product.price * (product.quantity || 1);

    setTotalCalories(prev => 
      newCheckedState ? prev + caloriesChange : prev - caloriesChange
    );
    setTotalPrice(prev => 
      newCheckedState ? prev + priceChange : prev - priceChange
    );
  };

  // Уменьшение количества
  const decreaseQuantity = (productId: string) => {
    setProducts(products.map(product => {
      if (product.id === productId && product.quantity > 1) {
        const newQuantity = product.quantity - 1;
        
        if (checkedProducts[productId]) {
          setTotalCalories(prev => prev - product.calories);
          setTotalPrice(prev => prev - product.price);
        }
        
        return { ...product, quantity: newQuantity };
      }
      return product;
    }));
  };

  // Увеличение количества
  const increaseQuantity = (productId: string) => {
    setProducts(products.map(product => {
      if (product.id === productId) {
        const newQuantity = product.quantity + 1;
        
        if (checkedProducts[productId]) {
          setTotalCalories(prev => prev + product.calories);
          setTotalPrice(prev => prev + product.price);
        }
        
        return { ...product, quantity: newQuantity };
      }
      return product;
    }));
  };

  // Форматирование цены
  const formatPrice = (price: number) => {
    if (i18n.language === 'en') {
      return `$${(price / 90).toFixed(2)}`;
    }
    return `${price} ₽`;
  };

  return (
    <div className="product-list">
      {products.map(product => (
        <div key={product.id} className="product-item">
          <input
            type="checkbox"
            checked={checkedProducts[product.id] || false}
            onChange={() => handleCheckboxChange(product.id)}
          />
          <span>{product.name}</span>
          <div className="product-controls">
            <button onClick={() => decreaseQuantity(product.id)}>-</button>
            <span>{product.quantity || 1}</span>
            <button onClick={() => increaseQuantity(product.id)}>+</button>
          </div>
          <span>{product.calories} {t('calories')}</span>
          <span>{formatPrice(product.price)}</span>
        </div>
      ))}
      <div className="totals">
        <div>{t('totalCalories')}: {totalCalories}</div>
        <div>{t('totalPrice')}: {formatPrice(totalPrice)}</div>
      </div>
    </div>
  );
};

export default ProductList; 