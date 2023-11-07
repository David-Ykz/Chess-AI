class MyButton extends React.Component {
    handleClick = () => {
        alert('Button clicked!');
    };

    render() {
        return (
            <button onClick={this.handleClick}>Click Me</button>
        );
    }
}

export default MyButton;