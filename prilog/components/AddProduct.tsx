import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';

interface AddProductProps {
  onAdd: (product: {
    id: string;
    name: string;
    calories: number;
    quantity: number;
    price: number;
  }) => void;
}

const AddProduct: React.FC<AddProductProps> = ({ onAdd }) => {
  const [name, setName] = useState('');
  const [calories, setCalories] = useState('');
  const [quantity, setQuantity] = useState('');
  const [price, setPrice] = useState('');
  const { t } = useTranslation();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !calories) return;

    const newProduct = {
      id: Date.now().toString(),
      name,
      calories: Number(calories),
      quantity: quantity ? Number(quantity) : 1,
      price: price ? Number(price) : 0
    };

    onAdd(newProduct);
    setName('');
    setCalories('');
    setQuantity('');
    setPrice('');
  };

  return (
    <form onSubmit={handleSubmit} className="add-product-form">
      <input
        type="text"
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder={t('productName')}
        required
      />
      <input
        type="number"
        value={calories}
        onChange={(e) => setCalories(e.target.value)}
        placeholder={t('calories')}
        required
      />
      <input
        type="number"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
        placeholder={t('quantity')}
      />
      <input
        type="number"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
        placeholder={t('price')}
      />
      <button type="submit">{t('addProduct')}</button>
    </form>
  );
};

export default AddProduct; 